package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.HargaRequestBody;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.OrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.HargaResponseBody;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderStatusResponse;
import com.haylaundry.service.backend.modules.ordermanagement.service.OrderService;
import com.haylaundry.service.backend.core.utils.HargaCucian;
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

    // Endpoint untuk menghitung harga
    @POST
    @Path("/hitung-harga")
    public Response hitungHarga(HargaRequestBody request) {
        try {
            PesananTipeCucian tipe = PesananTipeCucian.lookupLiteral(request.getTipeCucian());
            PesananJenisCucian jenis = PesananJenisCucian.lookupLiteral(request.getJenisCucian());

            double total = HargaCucian.hitungHargaTotal(tipe, jenis, request.getQty());
            return Response.ok(new HargaResponseBody(total)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
