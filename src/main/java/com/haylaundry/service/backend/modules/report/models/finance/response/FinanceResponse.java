package com.haylaundry.service.backend.modules.report.models.finance.response;

import java.time.LocalDate;

public class FinanceResponse {
    private String idLaporanKeuangan;   // ID laporan keuangan
    private String tglReport;
    private String totalPemasukan;
    private String totalPiutang;
    private String totalPengeluaran;
    private String totalKasMasuk;
    private String totalOmset;

    // Constructors
    public FinanceResponse(String idLaporanKeuangan, String tglReport, String totalPemasukan, String totalPiutang,
                           String totalPengeluaran, String totalKasMasuk, String totalOmset) {
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

    public String getTotalPemasukan() {
        return totalPemasukan;
    }

    public void setTotalPemasukan(String totalPemasukan) {
        this.totalPemasukan = totalPemasukan;
    }

    public String getTotalPiutang() {
        return totalPiutang;
    }

    public void setTotalPiutang(String totalPiutang) {
        this.totalPiutang = totalPiutang;
    }

    public String getTotalPengeluaran() {
        return totalPengeluaran;
    }

    public void setTotalPengeluaran(String totalPengeluaran) {
        this.totalPengeluaran = totalPengeluaran;
    }

    public String getTotalKasMasuk() {
        return totalKasMasuk;
    }

    public void setTotalKasMasuk(String totalKasMasuk) {
        this.totalKasMasuk = totalKasMasuk;
    }

    public String getTotalOmset() {
        return totalOmset;
    }

    public void setTotalOmset(String totalOmset) {
        this.totalOmset = totalOmset;
    }
}
