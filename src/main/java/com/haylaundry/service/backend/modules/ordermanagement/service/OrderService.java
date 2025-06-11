package com.haylaundry.service.backend.modules.ordermanagement.service;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.order.OrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderRepository orderRepository;

    public List<OrderResponse> getAllOrders() {
        return orderRepository.getAll();
    }

    public OrderResponse getOrderById(String idPesanan) {
        return orderRepository.getById(idPesanan);
    }

    public OrderResponse getOrderBynoFaktur(String nomor) {
        return orderRepository.getByNoFaktur(nomor);
    }


    public OrderResponse createOrder(OrderRequest request) {
        return orderRepository.create(request);
    }

    public OrderStatusResponse updateOrderStatus(String idPesanan, String statusOrder) {
        return orderRepository.updateStatus(idPesanan, statusOrder);
    }

    public OrderStatusBayar updateBayarStatus(String idPesanan, String statusBayar) {
        return orderRepository.updateBayarStatus(idPesanan, statusBayar);
    }

    public void deleteOrder(String idPesanan) {
        orderRepository.deleteById(idPesanan);
    }


    public boolean softDeleteOrder(String idPesanan) {
        return orderRepository.softDeleteById(idPesanan);
    }

}
