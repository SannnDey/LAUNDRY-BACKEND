package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

import java.util.List;

public class PesananSatuanResponse {
    private String idPesananSatuan;
    private String idCustomer;
    private String noFaktur;
    private String namaCustomer;
    private String customerPhone;
    private String tipePembayaran;
    private String statusBayar;
    private String statusOrder;
    private String totalHarga;
    private String tglMasuk;
    private String tglSelesai;
    private String catatan;
    private List<ItemPesananSatuanResponse> items;

    public PesananSatuanResponse() {
    }

    public PesananSatuanResponse(String idPesananSatuan, String idCustomer, String noFaktur, String namaCustomer, String customerPhone, String tipePembayaran,
                                 String statusBayar, String statusOrder, String totalHarga,
                                 String tglMasuk, String tglSelesai, String catatan,
                                 List<ItemPesananSatuanResponse> items) {
        this.idPesananSatuan = idPesananSatuan;
        this.idCustomer = idCustomer;
        this.noFaktur = noFaktur;
        this.namaCustomer = namaCustomer;
        this.customerPhone = customerPhone;
        this.tipePembayaran = tipePembayaran;
        this.statusBayar = statusBayar;
        this.statusOrder = statusOrder;
        this.totalHarga = totalHarga;
        this.tglMasuk = tglMasuk;
        this.tglSelesai = tglSelesai;
        this.catatan = catatan;
        this.items = items;
    }

    public String getIdPesananSatuan() {
        return idPesananSatuan;
    }

    public void setIdPesananSatuan(String idPesananSatuan) {
        this.idPesananSatuan = idPesananSatuan;
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

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getTglMasuk() {
        return tglMasuk;
    }

    public void setTglMasuk(String tglMasuk) {
        this.tglMasuk = tglMasuk;
    }

    public String getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(String tglSelesai) {
        this.tglSelesai = tglSelesai;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<ItemPesananSatuanResponse> getItems() {
        return items;
    }

    public void setItems(List<ItemPesananSatuanResponse> items) {
        this.items = items;
    }
}