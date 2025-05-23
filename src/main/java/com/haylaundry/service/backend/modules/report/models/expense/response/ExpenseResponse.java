package com.haylaundry.service.backend.modules.report.models.expense.response;

import java.time.LocalDateTime;

public class ExpenseResponse {
    private String idPengeluaran;
    private String jenisPengeluaran;
    private Double nominal;
    private String catatan;

    public ExpenseResponse () {

    }

    public ExpenseResponse(String idPengeluaran, String jenisPengeluaran, Double nominal, String catatan, LocalDateTime tglPengeluaran, LocalDateTime updatedAt) {
        this.idPengeluaran = idPengeluaran;
        this.jenisPengeluaran = jenisPengeluaran;
        this.nominal = nominal;
        this.catatan = catatan;
        this.tglPengeluaran = tglPengeluaran;
        this.updatedAt = updatedAt;
    }

    private LocalDateTime tglPengeluaran;
    private LocalDateTime updatedAt;





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

    public Double getNominal() {
        return nominal;
    }

    public void setNominal(Double nominal) {
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
