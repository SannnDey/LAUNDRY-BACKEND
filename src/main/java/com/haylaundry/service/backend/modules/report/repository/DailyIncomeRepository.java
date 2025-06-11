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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("id-ID"));
        DecimalFormat formatterRibuan = new DecimalFormat("#,###");

        try {
            LocalDate tglReport = LocalDate.parse(dateString, formatter);

            LaporanPemasukanHarianRecord report = jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                    .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.cast(Date.class).eq(Date.valueOf(tglReport))) // Using java.sql.Date for comparison
                    .fetchOne();

            if (report == null) {
                throw new IllegalArgumentException("Laporan tidak ditemukan untuk tanggal: " + tglReport);
            }

            String formattedTotalPemasukan = formatterRibuan.format(report.getTotalPemasukan());
            String formattedTotalPiutang = formatterRibuan.format(report.getTotalPiutang());
            String formattedTotalPengeluaran = formatterRibuan.format(report.getTotalPengeluaran());
            String formattedTotalKasMasuk = formatterRibuan.format(report.getTotalKasMasuk());
            String formattedTotalOmset = formatterRibuan.format(report.getTotalOmset());

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
            throw new IllegalArgumentException("Invalid date format. Please use the format 'dd MMMM yyyy' (e.g., '24 Mei 2025').", e);
        }
    }


    public void createLaporan(LocalDate tglReport) {
        DecimalFormat formatter = new DecimalFormat("#,###");

        LaporanPemasukanHarianRecord existingReport = jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.eq(tglReport))
                .fetchOne();

        double totalPemasukan = hitungTotalPemasukan(tglReport);
        double totalPiutang = hitungTotalPiutang(tglReport);
        double totalPengeluaran = hitungTotalPengeluaran(tglReport);
        double totalKasMasuk = totalPemasukan - totalPengeluaran;
        double totalOmset = totalPemasukan + totalPiutang - totalPengeluaran;

        String formattedTotalPemasukan = formatter.format(totalPemasukan);
        String formattedTotalPiutang = formatter.format(totalPiutang);
        String formattedTotalPengeluaran = formatter.format(totalPengeluaran);
        String formattedTotalKasMasuk = formatter.format(totalKasMasuk);
        String formattedTotalOmset = formatter.format(totalOmset);

        if (existingReport != null) {
            existingReport.setTotalPemasukan(totalPemasukan);
            existingReport.setTotalPiutang(totalPiutang);
            existingReport.setTotalPengeluaran(totalPengeluaran);
            existingReport.setTotalKasMasuk(totalKasMasuk);
            existingReport.setTotalOmset(totalOmset);
            existingReport.store();  // Simpan update
        } else {
            String idLaporanHarian = UuidCreator.getTimeOrderedEpoch().toString();

            LaporanPemasukanHarianRecord laporan = jooq.newRecord(Tables.LAPORAN_PEMASUKAN_HARIAN);
            laporan.setIdLaporanHarian(idLaporanHarian);
            laporan.setTglReport(tglReport);
            laporan.setTotalPemasukan(totalPemasukan);
            laporan.setTotalPiutang(totalPiutang);
            laporan.setTotalPengeluaran(totalPengeluaran);
            laporan.setTotalKasMasuk(totalKasMasuk);
            laporan.setTotalOmset(totalOmset);
            laporan.store();
        }

        rekapLaporanKeuanganBulanan(tglReport);
    }


    public void rekapLaporanKeuanganBulanan(LocalDate tglReport) {
        LocalDate firstOfMonth = tglReport.withDayOfMonth(1);
        LocalDate lastOfMonth = tglReport.withDayOfMonth(tglReport.lengthOfMonth());

        double totalPemasukanBulanan = hitungTotalPemasukanBulanan(firstOfMonth, lastOfMonth);
        double totalPiutangBulanan = hitungTotalPiutangBulanan(firstOfMonth, lastOfMonth);
        double totalPengeluaranBulanan = hitungTotalPengeluaranBulanan(firstOfMonth, lastOfMonth);
        double totalKasMasukBulanan = totalPemasukanBulanan - totalPengeluaranBulanan;
        double totalOmsetBulanan = totalPemasukanBulanan + totalPiutangBulanan - totalPengeluaranBulanan;

        String formattedDate = firstOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        LaporanKeuanganRecord existingMonthlyReport = jooq.selectFrom(Tables.LAPORAN_KEUANGAN)
                .where(Tables.LAPORAN_KEUANGAN.TGL_REPORT.eq(formattedDate))
                .fetchOne();

        if (existingMonthlyReport != null) {
            existingMonthlyReport.setTotalPemasukan(totalPemasukanBulanan);
            existingMonthlyReport.setTotalPiutang(totalPiutangBulanan);
            existingMonthlyReport.setTotalPengeluaran(totalPengeluaranBulanan);
            existingMonthlyReport.setTotalKasMasuk(totalKasMasukBulanan);
            existingMonthlyReport.setTotalOmset(totalOmsetBulanan);
            existingMonthlyReport.store();
        } else {
            String idLaporanKeuangan = UuidCreator.getTimeOrderedEpoch().toString();

            LaporanKeuanganRecord laporanBulanan = jooq.newRecord(Tables.LAPORAN_KEUANGAN);

            laporanBulanan.setIdLaporanKeuangan(idLaporanKeuangan);
            laporanBulanan.setTglReport(formattedDate);
            laporanBulanan.setTotalPemasukan(totalPemasukanBulanan);
            laporanBulanan.setTotalPiutang(totalPiutangBulanan);
            laporanBulanan.setTotalPengeluaran(totalPengeluaranBulanan);
            laporanBulanan.setTotalKasMasuk(totalKasMasukBulanan);
            laporanBulanan.setTotalOmset(totalOmsetBulanan);

            laporanBulanan.store();
        }
    }


    private double hitungTotalPemasukanBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.between(startOfMonth, endOfMonth))
                .fetch()
                .stream()
                .mapToDouble(LaporanPemasukanHarianRecord::getTotalPemasukan)
                .sum();
    }


    private double hitungTotalPiutangBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.between(startOfMonth, endOfMonth))
                .fetch()
                .stream()
                .mapToDouble(LaporanPemasukanHarianRecord::getTotalPiutang)
                .sum();
    }


    private double hitungTotalPengeluaranBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return jooq.selectFrom(Tables.LAPORAN_PEMASUKAN_HARIAN)
                .where(Tables.LAPORAN_PEMASUKAN_HARIAN.TGL_REPORT.between(startOfMonth, endOfMonth))
                .fetch()
                .stream()
                .mapToDouble(LaporanPemasukanHarianRecord::getTotalPengeluaran)
                .sum();
    }


    private double hitungTotalKasMasukBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return hitungTotalPemasukanBulanan(startOfMonth, endOfMonth) - hitungTotalPengeluaranBulanan(startOfMonth, endOfMonth);
    }


    private double hitungTotalOmsetBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return hitungTotalPemasukanBulanan(startOfMonth, endOfMonth) + hitungTotalPiutangBulanan(startOfMonth, endOfMonth) - hitungTotalPengeluaranBulanan(startOfMonth, endOfMonth);
    }


    private double hitungTotalPemasukan(LocalDate tglReport) {
        List<PesananRecord> pesananRecords = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.STATUS_BAYAR.eq(PesananStatusBayar.Lunas))
                .and(Tables.PESANAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        List<DetailPesananSatuanRecord> detailPesananSatuanRecords = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR.eq(DetailPesananSatuanStatusBayar.Lunas))
                .and(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        double totalPemasukanPesanan = pesananRecords.stream()
                .mapToDouble(record -> record.get(Tables.PESANAN.HARGA, Double.class))
                .sum();

        double totalPemasukanDetailPesananSatuan = detailPesananSatuanRecords.stream()
                .mapToDouble(record -> record.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA, Double.class))
                .sum();

        return totalPemasukanPesanan + totalPemasukanDetailPesananSatuan;
    }

    private double hitungTotalPiutang(LocalDate tglReport) {
        List<PesananRecord> pesananRecords = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.STATUS_BAYAR.eq(PesananStatusBayar.Belum_Lunas))
                .and(Tables.PESANAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        List<DetailPesananSatuanRecord> detailPesananSatuanRecords = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR.eq(DetailPesananSatuanStatusBayar.Belum_Lunas))
                .and(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK.between(
                        tglReport.atStartOfDay(),
                        tglReport.atTime(23, 59, 59)))
                .fetch();

        double totalPiutangPesanan = pesananRecords.stream()
                .mapToDouble(record -> record.get(Tables.PESANAN.HARGA, Double.class))
                .sum();

        double totalPiutangDetailPesananSatuan = detailPesananSatuanRecords.stream()
                .mapToDouble(record -> record.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA, Double.class))
                .sum();

        return totalPiutangPesanan + totalPiutangDetailPesananSatuan;
    }

    private double hitungTotalPengeluaran(LocalDate tglReport) {
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
