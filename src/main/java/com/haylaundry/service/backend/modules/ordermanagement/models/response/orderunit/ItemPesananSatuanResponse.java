package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

public class ItemPesananSatuanResponse {
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private int qty;

    public ItemPesananSatuanResponse() {
    }

    public ItemPesananSatuanResponse(String kategoriBarang, String ukuran, String jenisLayanan,
                                     int qty) {
        this.kategoriBarang = kategoriBarang;
        this.ukuran = ukuran;
        this.jenisLayanan = jenisLayanan;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

}
