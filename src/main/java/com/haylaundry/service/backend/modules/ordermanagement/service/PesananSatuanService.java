package com.haylaundry.service.backend.modules.ordermanagement.service;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PesananSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PriceOrderSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.PesananSatuanResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.PriceOrderSatuanResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.ItemPesananSatuanRepository;
import com.haylaundry.service.backend.modules.ordermanagement.repository.PesananSatuanRepository;
import com.haylaundry.service.backend.modules.ordermanagement.repository.PriceOrderSatuanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PesananSatuanService {

    @Inject
    ItemPesananSatuanRepository detailRepository;

    @Inject
    PesananSatuanRepository pesananRepository;

    @Inject
    PriceOrderSatuanRepository itemsRepository;

    // 1. Tambah item satuan (tanpa pesanan)
    public String createPrice(PriceOrderSatuanRequest request) {
        return itemsRepository.createPrice(request);
    }

    // 2. Buat pesanan satuan & assign item
    public void createPesananSatuan(PesananSatuanRequest request) {
        pesananRepository.createPesananSatuan(request);
    }

    // 3. Update status bayar
    public OrderUnitStatusBayar updateStatusBayar(String idDetail, String statusBayar) {
        return pesananRepository.updateStatusBayar(idDetail, statusBayar);
    }

    // 4. Update status order
    public OrderUnitStatusResponse updateStatusOrderUnit(String idDetail, String statusOrder) {
        return pesananRepository.updateStatusOrder(idDetail, statusOrder);
    }

    // 5. Soft delete
    public boolean softDeleteOrderUnitById(String idDetail) {
        return pesananRepository.softDeleteOrderUnitById(idDetail);
    }

    // ✅ 6. Get All Pesanan Satuan + List Item
    public List<PesananSatuanResponse> getAllOrderUnit() {
        return pesananRepository.getAllPesananSatuan();
    }

    // ✅ 7. Get Pesanan Satuan By No Faktur + List Item
    public PesananSatuanResponse getOrderUnitByNoFaktur(String noFaktur) {
        return pesananRepository.getPesananSatuanByNoFaktur(noFaktur);
    }

    public List<PriceOrderSatuanResponse> getAllPrice() {return itemsRepository.getAll(); }

    public void updatePrice(String idPriceSatuan, PriceOrderSatuanRequest request) {
        itemsRepository.updatePrice(idPriceSatuan, request);
    }

}
