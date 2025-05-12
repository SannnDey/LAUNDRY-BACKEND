package com.haylaundry.service.backend.masterdata.category.order.controller;

import com.haylaundry.service.backend.masterdata.category.order.models.request.OrderRequest;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderResponse;
import com.haylaundry.service.backend.masterdata.category.order.service.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/order")
public class OrderController {
    @Inject
    private OrderService orderService;

    // ✅ Endpoint untuk mengambil semua pesanan
    @GET
    public Response getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return Response.ok(orders).build();
    }

    // ✅ Endpoint untuk membuat pesanan baru
    @POST
    public Response createOrder(OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return Response.status(Response.Status.CREATED).entity(orderResponse).build();
    }
}
