package com.haylaundry.service.backend.modules.report.models.dailyincome.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyIncomeResponse {

    private String idLaporanHarian;
    private LocalDate tglReport;
    private double totalPemasukan;
    private double totalPiutang;
    private double totalPengeluaran;
    private double totalKasMasuk;
    private double totalOmset;

    // Constructors
    public DailyIncomeResponse(String idLaporanHarian, LocalDate tglReport, double totalPemasukan, double totalPiutang,
                                          double totalPengeluaran, double totalKasMasuk, double totalOmset) {
        this.idLaporanHarian = idLaporanHarian;
        this.tglReport = tglReport;
        this.totalPemasukan = totalPemasukan;
        this.totalPiutang = totalPiutang;
        this.totalPengeluaran = totalPengeluaran;
        this.totalKasMasuk = totalKasMasuk;
        this.totalOmset = totalOmset;
    }

    // Getters and Setters

    public String getIdLaporanHarian() {
        return idLaporanHarian;
    }

    public void setIdLaporanHarian(String idLaporanHarian) {
        this.idLaporanHarian = idLaporanHarian;
    }
    public LocalDate getTglReport() {
        return tglReport;
    }

    public void setTglReport(LocalDate tglReport) {
        this.tglReport = tglReport;
    }

    public double getTotalPemasukan() {
        return totalPemasukan;
    }

    public void setTotalPemasukan(double totalPemasukan) {
        this.totalPemasukan = totalPemasukan;
    }

    public double getTotalPiutang() {
        return totalPiutang;
    }

    public void setTotalPiutang(double totalPiutang) {
        this.totalPiutang = totalPiutang;
    }

    public double getTotalPengeluaran() {
        return totalPengeluaran;
    }

    public void setTotalPengeluaran(double totalPengeluaran) {
        this.totalPengeluaran = totalPengeluaran;
    }

    public double getTotalKasMasuk() {
        return totalKasMasuk;
    }

    public void setTotalKasMasuk(double totalKasMasuk) {
        this.totalKasMasuk = totalKasMasuk;
    }

    public double getTotalOmset() {
        return totalOmset;
    }

    public void setTotalOmset(double totalOmset) {
        this.totalOmset = totalOmset;
    }
}
