package com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit;

public class OrderUnitStatusBayar {
    private String idPesananSatuan;
    private String noFaktur;
    private String statusBayar;

    public OrderUnitStatusBayar(String idPesananSatuan, String noFaktur, String statusBayar) {
        this.idPesananSatuan = idPesananSatuan;
        this.noFaktur = noFaktur;
        this.statusBayar = statusBayar;
    }

    public String getIdPesananSatuan() {
        return idPesananSatuan;
    }

    public void setIdPesananSatuan(String idPesananSatuan) {
        this.idPesananSatuan = idPesananSatuan;
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
