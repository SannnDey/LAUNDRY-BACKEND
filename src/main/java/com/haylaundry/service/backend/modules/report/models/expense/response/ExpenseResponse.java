package com.haylaundry.service.backend.modules.report.models.expense.response;

import java.time.LocalDateTime;

public class ExpenseResponse {
    private String idPengeluaran;
    private String jenisPengeluaran;
    private String nominal;
    private String catatan;
    private LocalDateTime tglPengeluaran;

    public ExpenseResponse () {

    }

    public ExpenseResponse(String idPengeluaran, String jenisPengeluaran, String nominal, String catatan, LocalDateTime tglPengeluaran) {
        this.idPengeluaran = idPengeluaran;
        this.jenisPengeluaran = jenisPengeluaran;
        this.nominal = nominal;
        this.catatan = catatan;
        this.tglPengeluaran = tglPengeluaran;
    }

    public String getIdPengeluaran() {
        return idPengeluaran;
    }

    public void setIdPengeluaran(String idPengeluaran) {
        this.idPengeluaran = idPengeluaran;
    }

    public String getJenisPengeluaran() {
        return jenisPengeluaran;
    }

    public void setJenisPengeluaran(String jenisPengeluaran) {
        this.jenisPengeluaran = jenisPengeluaran;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getTglPengeluaran() {
        return tglPengeluaran;
    }

    public void setTglPengeluaran(LocalDateTime tglPengeluaran) {
        this.tglPengeluaran = tglPengeluaran;
    }

}
