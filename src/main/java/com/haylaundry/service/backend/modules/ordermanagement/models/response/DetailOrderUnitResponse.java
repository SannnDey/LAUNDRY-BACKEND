package com.haylaundry.service.backend.modules.ordermanagement.models.response;

import java.time.LocalDateTime;
import java.util.List;

public class DetailOrderUnitResponse {
    private String idDetail;
    private String idCustomer;
    private String noFaktur;
    private String namaCustomer;
    private String customerPhone;
    private String tipePembayaran;
    private String statusBayar;
    private String statusOrder;
    private LocalDateTime tglMasuk;
    private LocalDateTime tglSelesai;
    private String catatan;
    private LocalDateTime deletedAt;


    public DetailOrderUnitResponse() {

    }

    public DetailOrderUnitResponse(String idDetail, String idCustomer, String noFaktur, String namaCustomer, String customerPhone, String tipePembayaran, String statusBayar, String statusOrder, LocalDateTime tglMasuk, LocalDateTime tglSelesai, String catatan, LocalDateTime deletedAt) {
        this.idDetail = idDetail;
        this.idCustomer = idCustomer;
        this.noFaktur = noFaktur;
        this.namaCustomer = namaCustomer;
        this.customerPhone = customerPhone;
        this.tipePembayaran = tipePembayaran;
        this.statusBayar = statusBayar;
        this.statusOrder = statusOrder;
        this.tglMasuk = tglMasuk;
        this.tglSelesai = tglSelesai;
        this.catatan = catatan;
        this.deletedAt = deletedAt;
    }



    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getNoFaktur() {
        return noFaktur;
    }

    public void setNoFaktur(String noFaktur) {
        this.noFaktur = noFaktur;
    }

    public String getNamaCustomer() {
        return namaCustomer;
    }

    public void setNamaCustomer(String namaCustomer) {
        this.namaCustomer = namaCustomer;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTipePembayaran() {
        return tipePembayaran;
    }

    public void setTipePembayaran(String tipePembayaran) {
        this.tipePembayaran = tipePembayaran;
    }

    public String getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public LocalDateTime getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(LocalDateTime tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public LocalDateTime getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(LocalDateTime tglSelesai) {
        this.tglSelesai = tglSelesai;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
