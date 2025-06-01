package com.haylaundry.service.backend.modules.report.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanStatusOrder;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder;
import com.haylaundry.service.backend.jooq.gen.tables.records.*;
import com.haylaundry.service.backend.modules.report.models.dailyincome.response.DailyIncomeResponse;
import java.text.DecimalFormat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class DailyIncomeRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;


    public DailyIncomeResponse getLaporanByDate(String dateString) {
        // Define the date format and specify the Indonesian locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("id-ID"));

        // Create a DecimalFormat instance to format totals with thousand separator
        DecimalFormat formatterRibuan = new DecimalFormat("#,###");

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

            // Format the totals with the thousand separator
            String formattedTotalPemasukan = formatterRibuan.format(report.getTotalPemasukan());
            String formattedTotalPiutang = formatterRibuan.format(report.getTotalPiutang());
            String formattedTotalPengeluaran = formatterRibuan.format(report.getTotalPengeluaran());
            String formattedTotalKasMasuk = formatterRibuan.format(report.getTotalKasMasuk());
            String formattedTotalOmset = formatterRibuan.format(report.getTotalOmset());

            // Convert data from record to response model, including the formatted totals
            return new DailyIncomeResponse(
                    report.getIdLaporanHarian(),
                    report.getTglReport(),
                    formattedTotalPemasukan,
                    formattedTotalPiutang,
                    formattedTotalPengeluaran,
                    formattedTotalKasMasuk,
                    formattedTotalOmset
            );
        } catch (Exception e) {
            // Handle parsing errors or any other issues gracefully
            throw new IllegalArgumentException("Invalid date format. Please use the format 'dd MMMM yyyy' (e.g., '24 Mei 2025').", e);
        }
    }


    // Method to create or update daily income report and rekap to monthly report
    public void createLaporan(LocalDate tglReport) {
        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Cek apakah laporan harian sudah ada untuk tanggal yang sama
        LaporanPemasukanHarianRecord existingReport = jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.eq(tglReport))
                .fetchOne();

        // Menghitung total pemasukan, piutang, pengeluaran, kas masuk, dan omset
        double totalPemasukan = hitungTotalPemasukan(tglReport);
        double totalPiutang = hitungTotalPiutang(tglReport);
        double totalPengeluaran = hitungTotalPengeluaran(tglReport);
        double totalKasMasuk = totalPemasukan - totalPengeluaran;
        double totalOmset = totalPemasukan + totalPiutang - totalPengeluaran;

        // Format setiap total menggunakan pemisah ribuan
        String formattedTotalPemasukan = formatter.format(totalPemasukan);
        String formattedTotalPiutang = formatter.format(totalPiutang);
        String formattedTotalPengeluaran = formatter.format(totalPengeluaran);
        String formattedTotalKasMasuk = formatter.format(totalKasMasuk);
        String formattedTotalOmset = formatter.format(totalOmset);

        // Cek dan simpan laporan harian
        if (existingReport != null) {
            // Jika laporan harian sudah ada, lakukan update
            existingReport.setTotalPemasukan(totalPemasukan);
            existingReport.setTotalPiutang(totalPiutang);
            existingReport.setTotalPengeluaran(totalPengeluaran);
            existingReport.setTotalKasMasuk(totalKasMasuk);
            existingReport.setTotalOmset(totalOmset);
            existingReport.store();  // Simpan update
        } else {
            // Jika laporan harian tidak ada, buat laporan baru
            String idLaporanHarian = UuidCreator.getTimeOrderedEpoch().toString();

            LaporanPemasukanHarianRecord laporan = jooq.newRecord(Tables.LAPORAN_PEMASUKAN_HARIAN);
            laporan.setIdLaporanHarian(idLaporanHarian);
            laporan.setTglReport(tglReport);  // Menggunakan LocalDate
            laporan.setTotalPemasukan(totalPemasukan);
            laporan.setTotalPiutang(totalPiutang);
            laporan.setTotalPengeluaran(totalPengeluaran);
            laporan.setTotalKasMasuk(totalKasMasuk);
            laporan.setTotalOmset(totalOmset);
            laporan.store();  // Simpan laporan harian baru
        }

        // Rekap laporan keuangan bulanan otomatis setelah laporan harian dibuat atau diperbarui
        rekapLaporanKeuanganBulanan(tglReport);
    }


    public void rekapLaporanKeuanganBulanan(LocalDate tglReport) {
        // Ambil bulan dan tahun dari tanggal laporan
        LocalDate firstOfMonth = tglReport.withDayOfMonth(1);
        LocalDate lastOfMonth = tglReport.withDayOfMonth(tglReport.lengthOfMonth());

        // Menghitung total pemasukan, piutang, pengeluaran, kas masuk, dan omset untuk bulan ini
        double totalPemasukanBulanan = hitungTotalPemasukanBulanan(firstOfMonth, lastOfMonth);
        double totalPiutangBulanan = hitungTotalPiutangBulanan(firstOfMonth, lastOfMonth);
        double totalPengeluaranBulanan = hitungTotalPengeluaranBulanan(firstOfMonth, lastOfMonth);
        double totalKasMasukBulanan = totalPemasukanBulanan - totalPengeluaranBulanan;
        double totalOmsetBulanan = totalPemasukanBulanan + totalPiutangBulanan - totalPengeluaranBulanan;

        // Format firstOfMonth menjadi "YYYY-MM" untuk dibandingkan dengan tgl_report (yang disimpan dalam format VARCHAR(7))
        String formattedDate = firstOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Cek apakah laporan bulanan sudah ada
        LaporanKeuanganRecord existingMonthlyReport = jooq.selectFrom(Tables.LAPORAN_KEUANGAN)
                .where(Tables.LAPORAN_KEUANGAN.TGL_REPORT.eq(formattedDate))  // Laporan keuangan untuk bulan ini (format "YYYY-MM")
                .fetchOne();

        // Jika laporan bulanan sudah ada, lakukan update
        if (existingMonthlyReport != null) {
            existingMonthlyReport.setTotalPemasukan(totalPemasukanBulanan);
            existingMonthlyReport.setTotalPiutang(totalPiutangBulanan);
            existingMonthlyReport.setTotalPengeluaran(totalPengeluaranBulanan);
            existingMonthlyReport.setTotalKasMasuk(totalKasMasukBulanan);
            existingMonthlyReport.setTotalOmset(totalOmsetBulanan);
            existingMonthlyReport.store();  // Update laporan bulanan yang sudah ada
        } else {
            // Jika laporan bulanan belum ada, buat laporan baru
            // Generate unique ID for laporan_keuangan
            String idLaporanKeuangan = UuidCreator.getTimeOrderedEpoch().toString();

            // Buat objek LaporanKeuanganRecord baru
            LaporanKeuanganRecord laporanBulanan = jooq.newRecord(Tables.LAPORAN_KEUANGAN);

            // Set ID, tanggal, dan nilai lainnya
            laporanBulanan.setIdLaporanKeuangan(idLaporanKeuangan);  // Set ID for the report
            laporanBulanan.setTglReport(formattedDate);  // Tanggal laporan adalah 1 bulan ini dalam format "YYYY-MM"
            laporanBulanan.setTotalPemasukan(totalPemasukanBulanan);
            laporanBulanan.setTotalPiutang(totalPiutangBulanan);
            laporanBulanan.setTotalPengeluaran(totalPengeluaranBulanan);
            laporanBulanan.setTotalKasMasuk(totalKasMasukBulanan);
            laporanBulanan.setTotalOmset(totalOmsetBulanan);

            // Simpan laporan bulanan baru ke database
            laporanBulanan.store();
        }
    }



    // Method to calculate total pemasukan for the month
    private double hitungTotalPemasukanBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.between(startOfMonth, endOfMonth))
                .fetch()
                .stream()
                .mapToDouble(LaporanPemasukanHarianRecord::getTotalPemasukan)
                .sum();
    }

    // Method to calculate total piutang for the month
    private double hitungTotalPiutangBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.between(startOfMonth, endOfMonth))
                .fetch()
                .stream()
                .mapToDouble(LaporanPemasukanHarianRecord::getTotalPiutang)
                .sum();
    }

    // Method to calculate total pengeluaran for the month
    private double hitungTotalPengeluaranBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.between(startOfMonth, endOfMonth))
                .fetch()
                .stream()
                .mapToDouble(LaporanPemasukanHarianRecord::getTotalPengeluaran)
                .sum();
    }

    // Method to calculate total kas masuk for the month (Kas Masuk = Pemasukan - Pengeluaran)
    private double hitungTotalKasMasukBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return hitungTotalPemasukanBulanan(startOfMonth, endOfMonth) - hitungTotalPengeluaranBulanan(startOfMonth, endOfMonth);
    }

    // Method to calculate total omset for the month (Omset = Pemasukan + Piutang - Pengeluaran)
    private double hitungTotalOmsetBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return hitungTotalPemasukanBulanan(startOfMonth, endOfMonth) + hitungTotalPiutangBulanan(startOfMonth, endOfMonth) - hitungTotalPengeluaranBulanan(startOfMonth, endOfMonth);
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
