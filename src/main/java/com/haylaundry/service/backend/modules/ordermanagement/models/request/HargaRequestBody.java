package com.haylaundry.service.backend.modules.ordermanagement.models.request;

public class HargaRequestBody {
    private String tipeCucian;
    private String jenisCucian;
    private Double qty;

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

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

}
