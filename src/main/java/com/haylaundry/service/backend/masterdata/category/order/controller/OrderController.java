package com.haylaundry.service.backend.masterdata.category.order.controller;

import com.haylaundry.service.backend.masterdata.category.order.models.request.OrderRequest;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderResponse;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderStatusBayar;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderStatusResponse;
import com.haylaundry.service.backend.masterdata.category.order.service.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

    // ✅ Endpoint untuk memperbarui status pesanan
    @PUT
    @Path("/status")
    public Response updateOrderStatus(@QueryParam("idPesanan") String idPesanan,
                                      @QueryParam("statusOrder") String statusOrder) {
        OrderStatusResponse updatedOrder = orderService.updateOrderStatus(idPesanan, statusOrder);
        return Response.ok(updatedOrder).build();
    }


    @PUT
    @Path("/statusBayar")
    public Response updateBayarStatus(@QueryParam("idPesanan") String idPesanan,
                                      @QueryParam("statusBayar") String statusBayar) {
        OrderStatusBayar updatedOrder = orderService.updateBayarStatus(idPesanan, statusBayar);
        return Response.ok(updatedOrder).build();
    }


}
