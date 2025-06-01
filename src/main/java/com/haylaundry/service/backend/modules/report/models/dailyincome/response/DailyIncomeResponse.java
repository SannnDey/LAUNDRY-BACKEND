package com.haylaundry.service.backend.modules.report.models.dailyincome.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyIncomeResponse {

    private String idLaporanHarian;
    private LocalDate tglReport;
    private String totalPemasukan;
    private String totalPiutang;
    private String totalPengeluaran;
    private String totalKasMasuk;
    private String totalOmset;

    // Constructors
    public DailyIncomeResponse(String idLaporanHarian, LocalDate tglReport, String totalPemasukan, String totalPiutang,
                                          String totalPengeluaran, String totalKasMasuk, String totalOmset) {
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
