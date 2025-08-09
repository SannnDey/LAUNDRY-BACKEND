package com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit;

import java.util.List;

public class PesananSatuanRequest {
    private String idCustomer;
    private String tipePembayaran;
    private String statusBayar;
    private String statusOrder;
    private String catatan;
    private List<ItemPesananSatuanRequest> idItemList;

    // Getter & Setter
    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getTipePembayaran() {
        return tipePembayaran;
    }

    public void setTipePembayaran(String tipePembayaran) {
        this.tipePembayaran = tipePembayaran;
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

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public List<ItemPesananSatuanRequest> getIdItemList() {
        return idItemList;
    }

    public void setIdItemList(List<ItemPesananSatuanRequest> idItemList) {
        this.idItemList = idItemList;
    }
}
