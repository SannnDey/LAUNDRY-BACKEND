package com.haylaundry.service.backend.modules.ordermanagement.models.response.order;

import com.haylaundry.service.backend.jooq.gen.enums.HargaKiloanJenisCucian;
import com.haylaundry.service.backend.jooq.gen.enums.HargaKiloanTipeCucian;

public class HargaKiloanResponse {
    private String idHarga;
    private String tipeCucian;
    private String jenisCucian;
    private double hargaPerKg;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public HargaKiloanResponse() {}

    public HargaKiloanResponse(String idHarga, String tipeCucian, String jenisCucian, double hargaPerKg, String createdAt, String updatedAt) {
        this.idHarga = idHarga;
        this.tipeCucian = tipeCucian;
        this.jenisCucian = jenisCucian;
        this.hargaPerKg = hargaPerKg;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public HargaKiloanResponse(String idHarga, HargaKiloanTipeCucian tipeCucian, HargaKiloanJenisCucian jenisCucian, Double hargaPerKg, String createdAt, String updatedAt) {
    }

    // Getters and Setters
    public String getIdHarga() {
        return idHarga;
    }

    public void setIdHarga(String idHarga) {
        this.idHarga = idHarga;
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

    public double getHargaPerKg() {
        return hargaPerKg;
    }

    public void setHargaPerKg(double hargaPerKg) {
        this.hargaPerKg = hargaPerKg;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

