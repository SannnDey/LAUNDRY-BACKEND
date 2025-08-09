package com.haylaundry.service.backend.modules.ordermanagement.price.response;

import java.time.LocalDateTime;

public class PriceOrderResponse {
    private String idPrice;
    private String tipeCucian;
    private String jenisCucian;
    private String hargaperKg;

    public PriceOrderResponse(){
    }

    public PriceOrderResponse(String idPrice, String tipeCucian, String jenisCucian, String hargaperKg) {
        this.idPrice = idPrice;
        this.tipeCucian = tipeCucian;
        this.jenisCucian = jenisCucian;
        this.hargaperKg = hargaperKg;
    }

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

    public String getHargaperKg() {
        return hargaperKg;
    }

    public void setHargaperKg(String hargaperKg) {
        this.hargaperKg = hargaperKg;
    }

}
