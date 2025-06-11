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

    public List<DetailOrderUnitResponse> getAllOrderUnits() {
        return orderUnitRepository.getAllOrderUnit();
    }

    public DetailOrderUnitResponse getOrderUnitById(String idDetail) {
        return orderUnitRepository.getOrderUnitById(idDetail);
    }

    public DetailOrderUnitResponse getOrderUnitNoFaktur(String nomor) {
        return orderUnitRepository.getOrderUnitByNoFaktur(nomor);
    }


    public DetailOrderUnitResponse createOrderUnit(DetailOrderUnitRequest request) {
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


    public boolean softDeleteOrderUnitById(String idDetail) {
        return orderUnitRepository.softDeleteOrderUnitById(idDetail);
    }


}
