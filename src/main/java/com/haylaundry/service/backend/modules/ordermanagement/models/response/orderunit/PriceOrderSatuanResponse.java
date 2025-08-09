package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

public class PriceOrderSatuanResponse {
    private String idPriceSatuan;
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private Double hargaPerItem;

    public PriceOrderSatuanResponse() {
    }

    public PriceOrderSatuanResponse(String idPriceSatuan, String kategoriBarang, String ukuran, String jenisLayanan, Double hargaPerItem) {
        this.idPriceSatuan = idPriceSatuan;
        this.kategoriBarang = kategoriBarang;
        this.ukuran = ukuran;
        this.jenisLayanan = jenisLayanan;
        this.hargaPerItem = hargaPerItem;
    }

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

    public Double getHargaPerItem() {
        return hargaPerItem;
    }

    public void setHargaPerItem(Double hargaPerItem) {
        this.hargaPerItem = hargaPerItem;
    }

}
