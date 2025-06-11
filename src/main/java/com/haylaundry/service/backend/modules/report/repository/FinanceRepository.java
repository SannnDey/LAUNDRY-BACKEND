package com.haylaundry.service.backend.modules.report.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.LaporanKeuanganRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.LaporanPemasukanHarianRecord;
import com.haylaundry.service.backend.modules.report.models.finance.response.FinanceDateResponse;
import com.haylaundry.service.backend.modules.report.models.finance.response.FinanceResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.DecimalFormat;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@ApplicationScoped
public class FinanceRepository {

    @Inject
    private DSLContext jooq;


    public FinanceDateResponse getLaporanByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

        DecimalFormat formatterRibuan = new DecimalFormat("#,###");

        try {
            LocalDate startLocalDate = LocalDate.parse(startDate.trim(), formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate.trim(), formatter);

            double totalPemasukan = hitungTotalPemasukanBulanan(startLocalDate, endLocalDate);
            double totalPiutang = hitungTotalPiutangBulanan(startLocalDate, endLocalDate);
            double totalPengeluaran = hitungTotalPengeluaranBulanan(startLocalDate, endLocalDate);
            double totalKasMasuk = hitungTotalKasMasukBulanan(startLocalDate, endLocalDate);
            double totalOmset = hitungTotalOmsetBulanan(startLocalDate, endLocalDate);

            String formattedTotalPemasukan = formatterRibuan.format(totalPemasukan);
            String formattedTotalPiutang = formatterRibuan.format(totalPiutang);
            String formattedTotalPengeluaran = formatterRibuan.format(totalPengeluaran);
            String formattedTotalKasMasuk = formatterRibuan.format(totalKasMasuk);
            String formattedTotalOmset = formatterRibuan.format(totalOmset);

            String idLaporanKeuangan = UuidCreator.getTimeOrderedEpoch().toString();

            return new FinanceDateResponse(
                    idLaporanKeuangan,
                    startLocalDate.toString(),
                    endLocalDate.toString(),
                    formattedTotalPemasukan,
                    formattedTotalPiutang,
                    formattedTotalPengeluaran,
                    formattedTotalKasMasuk,
                    formattedTotalOmset
            );

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date format. Please use the format 'dd MMMM yyyy' (e.g., '21 Mei 2025 - 30 Mei 2025').", e);
        }
    }

    public FinanceResponse getLaporanByMonth(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));

        DecimalFormat formatterRibuan = new DecimalFormat("#,###");

        try {
            LocalDate tglReport = LocalDate.parse(date.trim() + " 01", DateTimeFormatter.ofPattern("MMMM yyyy dd", new Locale("id", "ID")));

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            String formattedDate = tglReport.format(outputFormatter);

            LaporanKeuanganRecord report = jooq.selectFrom(Tables.LAPORAN_KEUANGAN)
                    .where(Tables.LAPORAN_KEUANGAN.TGL_REPORT.eq(formattedDate))
                    .fetchOne();

            if (report == null) {
                throw new IllegalArgumentException("Laporan tidak ditemukan untuk tanggal: " + formattedDate);
            }

            String formattedTotalPemasukan = formatterRibuan.format(report.getTotalPemasukan());
            String formattedTotalPiutang = formatterRibuan.format(report.getTotalPiutang());
            String formattedTotalPengeluaran = formatterRibuan.format(report.getTotalPengeluaran());
            String formattedTotalKasMasuk = formatterRibuan.format(report.getTotalKasMasuk());
            String formattedTotalOmset = formatterRibuan.format(report.getTotalOmset());

            return new FinanceResponse(
                    report.getIdLaporanKeuangan(),
                    report.getTglReport(),
                    formattedTotalPemasukan,
                    formattedTotalPiutang,
                    formattedTotalPengeluaran,
                    formattedTotalKasMasuk,
                    formattedTotalOmset
            );
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date format. Please use the format 'MMMM yyyy' (e.g., 'Mei 2025').", e);
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

    // Method to calculate total omset for the month (Omset = Pemasukan + Piutang - Pengeluaran)
    private double hitungTotalOmsetBulanan(LocalDate startOfMonth, LocalDate endOfMonth) {
        return hitungTotalPemasukanBulanan(startOfMonth, endOfMonth) + hitungTotalPiutangBulanan(startOfMonth, endOfMonth) - hitungTotalPengeluaranBulanan(startOfMonth, endOfMonth);
    }
}
