package com.haylaundry.service.backend.modules.ordermanagement.models.response.order;


import com.haylaundry.service.backend.jooq.gen.enums.*;

import java.time.LocalDateTime;

public class OrderResponse {
    private String idPesanan;
    private String idCustomer;
    private String noFaktur;
    private String customerName;
    private String customerPhone;
    private String tipeCucian;
    private String jenisCucian;
    private Double qty;
    private String harga;
    private String tipePembayaran;
    private String statusBayar;
    private String statusOrder;
    private LocalDateTime tglMasuk;
    private LocalDateTime tglSelesai;
    private String catatan;
    private LocalDateTime deletedAt;

    // Default constructor
    public OrderResponse() {
    }

    // Full-args constructor
    public OrderResponse(String idPesanan, String idCustomer,String noFaktur, String customerName, String customerPhone, String tipeCucian,
                         String jenisCucian, Double qty, String harga,
                         String tipePembayaran, String statusBayar, String statusOrder,
                         LocalDateTime tglMasuk, LocalDateTime tglSelesai, String catatan, LocalDateTime deletedAt) {
        this.idPesanan = idPesanan;
        this.idCustomer = idCustomer;
        this.noFaktur = noFaktur;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.tipeCucian = tipeCucian;
        this.jenisCucian = jenisCucian;
        this.qty = qty;
        this.harga = harga;
        this.tipePembayaran = tipePembayaran;
        this.statusBayar = statusBayar;
        this.statusOrder = statusOrder;
        this.tglMasuk = tglMasuk;
        this.tglSelesai = tglSelesai;
        this.catatan = catatan;
        this.deletedAt = deletedAt;
    }

    // Getters and Setters

    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        this.idPesanan = idPesanan;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }


    public String getNoFaktur() {
        return noFaktur;
    }

    public void setNoFaktur(String noFaktur) {
        this.noFaktur = noFaktur;
    }

    public String getTipeCucian() {
        return tipeCucian;
    }

    public void setTipeCucian(String tipeCucian) {
        this.tipeCucian = tipeCucian;
    }

    public String getJenisCucian() {
        return jenisCucian;
    }

    public void setJenisCucian(String jenisCucian) {
        this.jenisCucian = jenisCucian;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
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