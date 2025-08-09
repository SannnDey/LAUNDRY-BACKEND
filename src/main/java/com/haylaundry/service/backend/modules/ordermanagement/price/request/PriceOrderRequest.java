package com.haylaundry.service.backend.modules.ordermanagement.price.request;

public class PriceOrderRequest {
    private String idPrice;
    private String tipeCucian;
    private String jenisCucian;
    private Double hargaperKg;

    public String getIdPrice() {
        return idPrice;
    }

    public void setIdPrice(String idPrice) {
        this.idPrice = idPrice;
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

    public Double getHargaperKg() {
        return hargaperKg;
    }

    public void setHargaperKg(Double hargaperKg) {
        this.hargaperKg = hargaperKg;
    }

}
