package com.haylaundry.service.backend.modules.report.models.finance.response;

import java.time.LocalDate;

public class FinanceResponse {
    private String idLaporanKeuangan;   // ID laporan keuangan
    private String tglReport;
    private double totalPemasukan;
    private double totalPiutang;
    private double totalPengeluaran;
    private double totalKasMasuk;
    private double totalOmset;

    // Constructors
    public FinanceResponse(String idLaporanKeuangan, String tglReport, double totalPemasukan, double totalPiutang,
                           double totalPengeluaran, double totalKasMasuk, double totalOmset) {
        this.idLaporanKeuangan = idLaporanKeuangan;
        this.tglReport = tglReport;
        this.totalPemasukan = totalPemasukan;
        this.totalPiutang = totalPiutang;
        this.totalPengeluaran = totalPengeluaran;
        this.totalKasMasuk = totalKasMasuk;
        this.totalOmset = totalOmset;
    }

    // Getters and Setters

    public String getIdLaporanKeuangan() {
        return idLaporanKeuangan;
    }

    public void setIdLaporanKeuangan(String idLaporanKeuangan) {
        this.idLaporanKeuangan = idLaporanKeuangan;
    }

    public String getTglReport() {
        return tglReport;
    }

    public void setTglReport(String tglReport) {
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
