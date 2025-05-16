package com.haylaundry.service.backend.modules.ordermanagement.service;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.OrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.DetailOrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.OrderUnitRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OrderUnitService {
    @Inject
    private OrderUnitRepository orderUnitRepository;

    // Mendapatkan semua detail order unit
    public List<DetailOrderUnitResponse> getAllOrderUnits() {
        return orderUnitRepository.getAllOrderUnit();
    }

    // Membuat order unit baru
    public OrderUnitResponse createOrderUnit(OrderUnitRequest request) {
        // Bisa ditambahkan validasi tambahan di sini jika perlu

        return orderUnitRepository.createOrderUnit(request);
    }

}
