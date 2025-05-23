package com.haylaundry.service.backend.modules.report.models.expense.request;

import java.time.LocalDateTime;

public class ExpenseRequest {
    private String jenisPengeluaran;
    private Double nominal;
    private String catatan;


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

    private LocalDateTime tglPengeluaran;
    private LocalDateTime updatedAt;
}
