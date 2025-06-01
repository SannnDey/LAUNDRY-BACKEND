package com.haylaundry.service.backend.modules.report.models.finance.response;

public class FinanceDateResponse {
    private String idLaporanKeuangan;   // ID laporan keuangan
    private String startDate;  // Start date of the range
    private String endDate;    // End date of the range
    private String totalPemasukan;
    private String totalPiutang;
    private String totalPengeluaran;
    private String totalKasMasuk;
    private String totalOmset;

    public FinanceDateResponse(String idLaporanKeuangan, String startDate, String endDate, String totalPemasukan, String totalPiutang, String totalPengeluaran, String totalKasMasuk, String totalOmset) {
        this.idLaporanKeuangan = idLaporanKeuangan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPemasukan = totalPemasukan;
        this.totalPiutang = totalPiutang;
        this.totalPengeluaran = totalPengeluaran;
        this.totalKasMasuk = totalKasMasuk;
        this.totalOmset = totalOmset;
    }



    public String getIdLaporanKeuangan() {
        return idLaporanKeuangan;
    }

    public void setIdLaporanKeuangan(String idLaporanKeuangan) {
        this.idLaporanKeuangan = idLaporanKeuangan;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
