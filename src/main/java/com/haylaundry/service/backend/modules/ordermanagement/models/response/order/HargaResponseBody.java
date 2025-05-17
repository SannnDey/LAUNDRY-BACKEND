package com.haylaundry.service.backend.modules.ordermanagement.models.response.order;

public class HargaResponseBody {
    private Double hargaTotal;


    public HargaResponseBody(Double hargaTotal) {
        this.hargaTotal = hargaTotal;
    }


    public Double getHargaTotal() {
        return hargaTotal;
    }

    public void setHargaTotal(Double hargaTotal) {
        this.hargaTotal = hargaTotal;
    }

}
