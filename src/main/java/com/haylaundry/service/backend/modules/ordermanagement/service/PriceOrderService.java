package com.haylaundry.service.backend.modules.ordermanagement.service;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PriceOrderSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.price.request.PriceOrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.price.response.PriceOrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.PriceOrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PriceOrderService {

    @Inject
    private PriceOrderRepository priceOrderRepository;

    // ✅ Ambil semua data harga
    public List<PriceOrderResponse> getAllPrices() {
        return priceOrderRepository.getAllPrices();
    }

    // ✅ Ambil harga berdasarkan tipe dan jenis cucian
    public PriceOrderResponse getHargaByTipeAndJenis(String tipeCucian, String jenisCucian) {
        return priceOrderRepository.getHargaByTipeAndJenis(tipeCucian, jenisCucian);
    }

    // ✅ Tambah atau update harga berdasarkan tipe dan jenis cucian
    public void createOrUpdateHarga(PriceOrderRequest request) {
        priceOrderRepository.upsertHarga(
                request.getTipeCucian(),
                request.getJenisCucian(),
                request.getHargaperKg()
        );
    }

    public void updateHargaById(String idPrice, Double hargaPerKg) {
        priceOrderRepository.updateHargaById(idPrice, hargaPerKg);
    }

    public boolean deleteHargaById(String idPrice) {
        return priceOrderRepository.hardDeleteById(idPrice);
    }

}
