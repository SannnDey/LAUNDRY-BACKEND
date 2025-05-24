package com.haylaundry.service.backend.modules.report.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanStatusOrder;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder;
import com.haylaundry.service.backend.jooq.gen.tables.records.DetailPesananSatuanRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.LaporanPemasukanHarianRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.PengeluaranRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.PesananRecord;
import com.haylaundry.service.backend.modules.report.models.dailyincome.response.DailyIncomeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class DailyIncomeRepository {

    @Inject
    private DSLContext jooq;

    public DailyIncomeResponse getLaporanByDate(String dateString) {
        // Define the date format and specify the Indonesian locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("id-ID"));

        try {
            // Parse the input date string to a LocalDate object
            LocalDate tglReport = LocalDate.parse(dateString, formatter);

            // Pastikan perbandingan tanggal dilakukan hanya berdasarkan tanggal
            LaporanPemasukanHarianRecord report = jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                    .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.cast(Date.class).eq(Date.valueOf(tglReport))) // Using java.sql.Date for comparison
                    .fetchOne(); // Fetch one entry

            if (report == null) {
                // If no report is found, throw an exception
                throw new IllegalArgumentException("Laporan tidak ditemukan untuk tanggal: " + tglReport);
            }

            // Convert data from record to response model
            return new DailyIncomeResponse(
                    report.getIdLaporanHarian(),
                    report.getTglReport(),
                    report.getTotalPemasukan(),
                    report.getTotalPiutang(),
                    report.getTotalPengeluaran(),
                    report.getTotalKasMasuk(),
                    report.getTotalOmset()
            );
        } catch (Exception e) {
            // Handle parsing errors or any other issues gracefully
            throw new IllegalArgumentException("Invalid date format. Please use the format 'dd MMMM yyyy' (e.g., '24 Mei 2025').", e);
        }
    }



    public void createLaporan(LocalDate tglReport) {
        // Cek apakah laporan sudah ada untuk tanggal yang sama
        LaporanPemasukanHarianRecord existingReport = jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.eq(tglReport))
                .fetchOne();

        // Menghitung total pemasukan, piutang, pengeluaran, kas masuk, dan omset
        double totalPemasukan = hitungTotalPemasukan(tglReport);
        double totalPiutang = hitungTotalPiutang(tglReport);
        double totalPengeluaran = hitungTotalPengeluaran(tglReport);
        double totalKasMasuk = totalPemasukan - totalPengeluaran;
        double totalOmset = totalPemasukan + totalPiutang - totalPengeluaran;

        if (existingReport != null) {
            // Jika laporan sudah ada, lakukan update
            existingReport.setTotalPemasukan(totalPemasukan);
            existingReport.setTotalPiutang(totalPiutang);
            existingReport.setTotalPengeluaran(totalPengeluaran);
            existingReport.setTotalKasMasuk(totalKasMasuk);
            existingReport.setTotalOmset(totalOmset);
            existingReport.store();  // Simpan update
        } else {
            // Jika laporan tidak ada, buat laporan baru
            String idLaporanHarian = UuidCreator.getTimeOrderedEpoch().toString();

            LaporanPemasukanHarianRecord laporan = jooq.newRecord(Tables.LAPORAN_PEMASUKAN_HARIAN);
            laporan.setIdLaporanHarian(idLaporanHarian);
            laporan.setTglReport(tglReport);  // Menggunakan LocalDate
            laporan.setTotalPemasukan(totalPemasukan);
            laporan.setTotalPiutang(totalPiutang);
            laporan.setTotalPengeluaran(totalPengeluaran);
            laporan.setTotalKasMasuk(totalKasMasuk);
            laporan.setTotalOmset(totalOmset);
            laporan.store();  // Simpan laporan baru
        }
    }


    private double hitungTotalPemasukan(LocalDate tglReport) {
        // Mengambil data pesanan yang sudah lunas pada tanggal tertentu
        List<PesananRecord> pesananRecords = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.STATUS_BAYAR.eq(PesananStatusBayar.Lunas))
                .and(Tables.PESANAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),   // Mulai dari jam 00:00
                        tglReport.atTime(23, 59, 59))) // Sampai jam 23:59:59
                .fetch();

        // Mengambil data detail pesanan satuan yang sudah lunas pada tanggal tertentu
        List<DetailPesananSatuanRecord> detailPesananSatuanRecords = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR.eq(DetailPesananSatuanStatusBayar.Lunas))
                .and(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),   // Mulai dari jam 00:00
                        tglReport.atTime(23, 59, 59))) // Sampai jam 23:59:59
                .fetch();

        // Menghitung total pemasukan dari pesanan
        double totalPemasukanPesanan = pesananRecords.stream()
                .mapToDouble(record -> record.get(Tables.PESANAN.HARGA, Double.class))
                .sum();

        // Menghitung total pemasukan dari detail pesanan satuan
        double totalPemasukanDetailPesananSatuan = detailPesananSatuanRecords.stream()
                .mapToDouble(record -> record.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA, Double.class))
                .sum();

        return totalPemasukanPesanan + totalPemasukanDetailPesananSatuan;
    }

    private double hitungTotalPiutang(LocalDate tglReport) {
        // Mengambil data pesanan yang belum lunas pada tanggal tertentu
        List<PesananRecord> pesananRecords = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.STATUS_BAYAR.eq(PesananStatusBayar.Belum_Lunas))
                .and(Tables.PESANAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        // Mengambil data detail pesanan satuan yang belum lunas pada tanggal tertentu
        List<DetailPesananSatuanRecord> detailPesananSatuanRecords = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR.eq(DetailPesananSatuanStatusBayar.Belum_Lunas))
                .and(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        // Menghitung total piutang dari pesanan
        double totalPiutangPesanan = pesananRecords.stream()
                .mapToDouble(record -> record.get(Tables.PESANAN.HARGA, Double.class))
                .sum();

        // Menghitung total piutang dari detail pesanan satuan
        double totalPiutangDetailPesananSatuan = detailPesananSatuanRecords.stream()
                .mapToDouble(record -> record.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA, Double.class))
                .sum();

        return totalPiutangPesanan + totalPiutangDetailPesananSatuan;
    }

    private double hitungTotalPengeluaran(LocalDate tglReport) {
        // Mengambil data pengeluaran pada tanggal tertentu
        List<PengeluaranRecord> records = jooq.selectFrom(Tables.PENGELUARAN)
                .where(Tables.PENGELUARAN.TGL_PENGELUARAN.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        return records.stream()
                .mapToDouble(record -> record.get(Tables.PENGELUARAN.NOMINAL, Double.class))
                .sum();
    }

}
