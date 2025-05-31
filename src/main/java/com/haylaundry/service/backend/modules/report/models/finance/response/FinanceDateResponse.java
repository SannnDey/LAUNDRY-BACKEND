package com.haylaundry.service.backend.modules.report.models.finance.response;

public class FinanceDateResponse {
    private String idLaporanKeuangan;   // ID laporan keuangan
    private String startDate;  // Start date of the range
    private String endDate;    // End date of the range
    private double totalPemasukan;
    private double totalPiutang;
    private double totalPengeluaran;
    private double totalKasMasuk;
    private double totalOmset;

    public FinanceDateResponse(String idLaporanKeuangan, String startDate, String endDate, double totalPemasukan, double totalPiutang, double totalPengeluaran, double totalKasMasuk, double totalOmset) {
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
