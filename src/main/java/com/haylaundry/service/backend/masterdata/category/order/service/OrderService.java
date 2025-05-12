package com.haylaundry.service.backend.masterdata.category.order.service;

import com.haylaundry.service.backend.masterdata.category.order.models.request.OrderRequest;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderResponse;
import com.haylaundry.service.backend.masterdata.category.order.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderRepository orderRepository;

    // ✅ Ambil semua data pesanan
    public List<OrderResponse> getAllOrders() {
        return orderRepository.getAll();
    }

    // ✅ Buat pesanan baru
    public OrderResponse createOrder(OrderRequest request) {
        return orderRepository.create(request);
    }
}
