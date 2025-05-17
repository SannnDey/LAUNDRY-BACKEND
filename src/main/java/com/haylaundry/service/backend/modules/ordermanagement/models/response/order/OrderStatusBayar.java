package com.haylaundry.service.backend.modules.ordermanagement.models.response.order;

import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar;

public class OrderStatusBayar {
    private String idPesanan;
    private String noFaktur;
    private String statusBayar;


    public OrderStatusBayar(String idPesanan, String noFaktur, String statusBayar) {
        this.idPesanan = idPesanan;
        this.noFaktur = noFaktur;
        this.statusBayar = statusBayar;
    }


    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        this.idPesanan = idPesanan;
    }

    public String getNoFaktur() {
        return noFaktur;
    }

    public void setNoFaktur(String noFaktur) {
        this.noFaktur = noFaktur;
    }

    public String getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }
}
