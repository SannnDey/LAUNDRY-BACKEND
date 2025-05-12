package com.haylaundry.service.backend.masterdata.category.order.service;

import com.haylaundry.service.backend.masterdata.category.order.models.request.OrderRequest;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderResponse;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderStatusBayar;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderStatusResponse;
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

    // ✅ Update status pesanan
    public OrderStatusResponse updateOrderStatus(String idPesanan, String statusOrder) {
        return orderRepository.updateStatus(idPesanan, statusOrder);
    }

    public OrderStatusBayar updateBayarStatus(String idPesanan, String statusBayar) {
        return orderRepository.updateBayarStatus(idPesanan, statusBayar);
    }

}
