package com.haylaundry.service.backend.modules.ordermanagement.models.response.order;

import java.time.LocalDateTime;
import com.haylaundry.service.backend.jooq.gen.enums.*;

public class OrderStatusResponse {
    private String idPesanan;
    private String noFaktur;
    private String tipeCucian;
    private String jenisCucian;
    private PesananStatusBayar statusBayar;
    private String statusOrder;
    private LocalDateTime tglSelesai;

    public OrderStatusResponse(String idPesanan, String noFaktur, String tipeCucian, String jenisCucian, PesananStatusBayar statusBayar, String statusOrder, LocalDateTime tglSelesai) {
        this.idPesanan = idPesanan;
        this.noFaktur = noFaktur;
        this.tipeCucian = tipeCucian;
        this.jenisCucian = jenisCucian;
        this.statusBayar = statusBayar;
        this.statusOrder = statusOrder;
        this.tglSelesai = tglSelesai;
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

    public PesananStatusBayar getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(PesananStatusBayar statusBayar) {
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
