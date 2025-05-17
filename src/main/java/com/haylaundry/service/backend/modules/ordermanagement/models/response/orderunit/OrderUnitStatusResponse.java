package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

import java.time.LocalDateTime;

public class OrderUnitStatusResponse {
    private String idDetail;
    private String noFaktur;
    private String statusBayar;
    private String statusOrder;

    public OrderUnitStatusResponse(String idDetail, String noFaktur, String statusBayar, String statusOrder, LocalDateTime tglSelesai) {
        this.idDetail = idDetail;
        this.noFaktur = noFaktur;
        this.statusBayar = statusBayar;
        this.statusOrder = statusOrder;
        this.tglSelesai = tglSelesai;
    }

    private LocalDateTime tglSelesai;



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

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public LocalDateTime getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(LocalDateTime tglSelesai) {
        this.tglSelesai = tglSelesai;
    }
}
