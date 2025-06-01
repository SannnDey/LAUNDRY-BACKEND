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


    // Method to get the report by date range
    public FinanceDateResponse getLaporanByDateRange(String startDate, String endDate) {
        // Define the date format and specify the Indonesian locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

        // Create a DecimalFormat instance to format totals with thousand separator
        DecimalFormat formatterRibuan = new DecimalFormat("#,###");

        try {
            // Parse the input start and end date strings to LocalDate objects
            LocalDate startLocalDate = LocalDate.parse(startDate.trim(), formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate.trim(), formatter);

            // Convert data from the date range to response model
            double totalPemasukan = hitungTotalPemasukanBulanan(startLocalDate, endLocalDate);
            double totalPiutang = hitungTotalPiutangBulanan(startLocalDate, endLocalDate);
            double totalPengeluaran = hitungTotalPengeluaranBulanan(startLocalDate, endLocalDate);
            double totalKasMasuk = hitungTotalKasMasukBulanan(startLocalDate, endLocalDate);
            double totalOmset = hitungTotalOmsetBulanan(startLocalDate, endLocalDate);

            // Format the totals with the thousand separator
            String formattedTotalPemasukan = formatterRibuan.format(totalPemasukan);
            String formattedTotalPiutang = formatterRibuan.format(totalPiutang);
            String formattedTotalPengeluaran = formatterRibuan.format(totalPengeluaran);
            String formattedTotalKasMasuk = formatterRibuan.format(totalKasMasuk);
            String formattedTotalOmset = formatterRibuan.format(totalOmset);

            // Create the report ID
            String idLaporanKeuangan = UuidCreator.getTimeOrderedEpoch().toString();  // Assuming UUID is used as the ID

            // Return the report data in the FinanceDateResponse model with formatted totals
            return new FinanceDateResponse(
                    idLaporanKeuangan,
                    startLocalDate.toString(),  // Set the report start date
                    endLocalDate.toString(),    // Set the report end date
                    formattedTotalPemasukan,    // Use formatted total
                    formattedTotalPiutang,      // Use formatted total
                    formattedTotalPengeluaran,  // Use formatted total
                    formattedTotalKasMasuk,     // Use formatted total
                    formattedTotalOmset        // Use formatted total
            );

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date format. Please use the format 'dd MMMM yyyy' (e.g., '21 Mei 2025 - 30 Mei 2025').", e);
        }
    }

    public FinanceResponse getLaporanByMonth(String date) {
        // Define the date format and specify the Indonesian locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));

        // Create a DecimalFormat instance to format totals with thousand separator
        DecimalFormat formatterRibuan = new DecimalFormat("#,###");

        try {
            // Parse the input date string to a LocalDate object by adding the first day of the month
            LocalDate tglReport = LocalDate.parse(date.trim() + " 01", DateTimeFormatter.ofPattern("MMMM yyyy dd", new Locale("id", "ID")));

            // Format it as 'YYYY-MM' to store only the year and month
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            String formattedDate = tglReport.format(outputFormatter);

            // Cek apakah laporan bulanan sudah ada
            LaporanKeuanganRecord report = jooq.selectFrom(Tables.LAPORAN_KEUANGAN)
                    .where(Tables.LAPORAN_KEUANGAN.TGL_REPORT.eq(formattedDate))  // Compare using 'YYYY-MM'
                    .fetchOne();

            if (report == null) {
                // If no report is found, return an error or an empty response
                throw new IllegalArgumentException("Laporan tidak ditemukan untuk tanggal: " + formattedDate);
            }

            // Format each total with thousand separators
            String formattedTotalPemasukan = formatterRibuan.format(report.getTotalPemasukan());
            String formattedTotalPiutang = formatterRibuan.format(report.getTotalPiutang());
            String formattedTotalPengeluaran = formatterRibuan.format(report.getTotalPengeluaran());
            String formattedTotalKasMasuk = formatterRibuan.format(report.getTotalKasMasuk());
            String formattedTotalOmset = formatterRibuan.format(report.getTotalOmset());

            // Convert data from record to response model with formatted totals
            return new FinanceResponse(
                    report.getIdLaporanKeuangan(),
                    report.getTglReport(),
                    formattedTotalPemasukan,  // Use formatted total
                    formattedTotalPiutang,    // Use formatted total
                    formattedTotalPengeluaran,// Use formatted total
                    formattedTotalKasMasuk,   // Use formatted total
                    formattedTotalOmset      // Use formatted total
            );
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date format. Please use the format 'MMMM yyyy' (e.g., 'Mei 2025').", e);
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
}
