package com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit;

public class PriceOrderSatuanRequest {
    private String idPriceSatuan;
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private double hargaPerItem;

    public String getIdPriceSatuan() {
        return idPriceSatuan;
    }

    public void setIdPriceSatuan(String idPriceSatuan) {
        this.idPriceSatuan = idPriceSatuan;
    }

    public String getKategoriBarang() {
        return kategoriBarang;
    }

    public void setKategoriBarang(String kategoriBarang) {
        this.kategoriBarang = kategoriBarang;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public String getJenisLayanan() {
        return jenisLayanan;
    }

    public void setJenisLayanan(String jenisLayanan) {
        this.jenisLayanan = jenisLayanan;
    }

    public double getHargaPerItem() {
        return hargaPerItem;
    }

    public void setHargaPerItem(double hargaPerItem) {
        this.hargaPerItem = hargaPerItem;
    }
}
