package com.haylaundry.service.backend.modules.ordermanagement.models.request.order;

public class HargaKiloanRequest {
    private String tipeCucian;     // Contoh: "Reguler 3 Hari"
    private String jenisCucian;    // Contoh: "Komplit"
    private double hargaPerKg;

    // Constructors
    public HargaKiloanRequest() {}

    public HargaKiloanRequest(String tipeCucian, String jenisCucian, double hargaPerKg) {
        this.tipeCucian = tipeCucian;
        this.jenisCucian = jenisCucian;
        this.hargaPerKg = hargaPerKg;
    }

    // Getters and Setters
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
}
