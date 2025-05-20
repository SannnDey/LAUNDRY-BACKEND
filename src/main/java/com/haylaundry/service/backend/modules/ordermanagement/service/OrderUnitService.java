package com.haylaundry.service.backend.modules.ordermanagement.service;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.DetailOrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.DetailOrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusResponse;
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

    public DetailOrderUnitResponse getOrderUnitById(String idDetail) {
        return orderUnitRepository.getOrderUnitById(idDetail);
    }


    // Membuat order unit baru
    public DetailOrderUnitResponse createOrderUnit(DetailOrderUnitRequest request) {
        // Bisa tambahkan validasi lain seperti validasi customer, dsb, atau langsung lempar ke repository
        return orderUnitRepository.createOrderUnit(request);
    }

    public OrderUnitStatusBayar updateStatusBayar (String idDetail, String statusBayar) {
        return orderUnitRepository.updateStatusBayar(idDetail, statusBayar);
    }

    public OrderUnitStatusResponse updateStatusOrderUnit (String idDetail, String statusOrder) {
        return orderUnitRepository.updateStatusOrder(idDetail, statusOrder);
    }

    public boolean deleteOrderUnitById(String idDetail) {
        return orderUnitRepository.deleteOrderUnitById(idDetail);
    }


}
