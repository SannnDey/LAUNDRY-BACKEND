package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

public class ItemPesananSatuanResponse {
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private String harga;
    private int qty;

    public ItemPesananSatuanResponse() {
    }

    public ItemPesananSatuanResponse(String kategoriBarang, String ukuran, String jenisLayanan, String harga, int qty) {
        this.kategoriBarang = kategoriBarang;
        this.ukuran = ukuran;
        this.jenisLayanan = jenisLayanan;
        this.harga = harga;
        this.qty = qty;
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
    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

}
