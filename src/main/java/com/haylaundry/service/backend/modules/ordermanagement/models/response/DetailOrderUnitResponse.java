package com.haylaundry.service.backend.modules.ordermanagement.models.response;

public class DetailOrderUnitResponse {
    private String idDetail;
    private String idPesananSatuan;
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private Double harga;
    private Double qty;

    public DetailOrderUnitResponse (){

    }

    public DetailOrderUnitResponse(String idDetail, String idPesananSatuan, String kategoriBarang, String ukuran, String jenisLayanan, Double harga, Double qty) {
        this.idDetail = idDetail;
        this.idPesananSatuan = idPesananSatuan;
        this.kategoriBarang = kategoriBarang;
        this.ukuran = ukuran;
        this.jenisLayanan = jenisLayanan;
        this.harga = harga;
        this.qty = qty;
    }



    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getIdPesananSatuan() {
        return idPesananSatuan;
    }

    public void setIdPesananSatuan(String idPesananSatuan) {
        this.idPesananSatuan = idPesananSatuan;
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

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }
}
