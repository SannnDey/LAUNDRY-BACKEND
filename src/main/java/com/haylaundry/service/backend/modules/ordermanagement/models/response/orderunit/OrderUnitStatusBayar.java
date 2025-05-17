package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanStatusBayar;

public class OrderUnitStatusBayar {
    private String idDetail;
    private String noFaktur;
    private String statusBayar;

    public OrderUnitStatusBayar(String idDetail, String noFaktur, String statusBayar) {
        this.idDetail = idDetail;
        this.noFaktur = noFaktur;
        this.statusBayar = statusBayar;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
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
