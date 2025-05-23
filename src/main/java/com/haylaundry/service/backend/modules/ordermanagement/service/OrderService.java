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

    // ✅ Ambil semua data pesanan
    public List<OrderResponse> getAllOrders() {
        return orderRepository.getAll();
    }

    // ✅ Ambil data pesanan berdasarkan ID
    public OrderResponse getOrderById(String idPesanan) {
        return orderRepository.getById(idPesanan);
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

    // ✅ Hapus pesanan (hard delete)
    public void deleteOrder(String idPesanan) {
        orderRepository.deleteById(idPesanan);
    }




}
