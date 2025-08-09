package com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit;

public class ItemPesananSatuanRequest {
    private String idPriceSatuan;
    private int qty;
    public String getIdPriceSatuan() {
        return idPriceSatuan;
    }

    public void setIdPriceSatuan(String idPriceSatuan) {
        this.idPriceSatuan = idPriceSatuan;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
