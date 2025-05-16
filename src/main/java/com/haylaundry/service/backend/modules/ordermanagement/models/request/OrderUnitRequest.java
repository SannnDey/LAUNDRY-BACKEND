package com.haylaundry.service.backend.modules.ordermanagement.models.request;

import java.util.List;

public class OrderUnitRequest {
    private String idDetail;
    private List<DetailOrderUnitRequest> details;
    private String kategoriBarang;
    private String ukuran;
    private String jenisLayanan;
    private Double harga;
    private Integer qty;



    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public List<DetailOrderUnitRequest> getDetails() {
        return details;
    }

    public void setDetails(List<DetailOrderUnitRequest> details) {
        this.details = details;
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
