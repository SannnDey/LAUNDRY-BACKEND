package com.haylaundry.service.backend.modules.ordermanagement.models.response;

public class OrderUnitResponse {
    private String idPesananSatuan;
    private String idDetail;
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private Double harga;
    private Integer qty;

    public OrderUnitResponse() {
    }

    public OrderUnitResponse(String idPesananSatuan, String idDetail, String kategoriBarang, String ukuran,
                             String jenisLayanan, Double harga, Integer qty) {
        this.idPesananSatuan = idPesananSatuan;
        this.idDetail = idDetail;
        this.kategoriBarang = kategoriBarang;
        this.ukuran = ukuran;
        this.jenisLayanan = jenisLayanan;
        this.harga = harga;
        this.qty = qty;
    }

    public String getIdPesananSatuan() {
        return idPesananSatuan;
    }

    public void setIdPesananSatuan(String idPesananSatuan) {
        this.idPesananSatuan = idPesananSatuan;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
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

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
