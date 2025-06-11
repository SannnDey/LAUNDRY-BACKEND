package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.order.HargaRequestBody;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.order.OrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.HargaResponseBody;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusResponse;
import com.haylaundry.service.backend.modules.ordermanagement.service.OrderService;
import com.haylaundry.service.backend.core.utils.PriceOrder;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/order")
public class OrderController {
    @Inject
    private OrderService orderService;

    @GET
    public Response getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return Response.ok(orders).build();
    }


    @GET
    @Path("/detail")
    public Response getOrderById(@QueryParam("idPesanan") String idPesanan) {
        if (idPesanan == null || idPesanan.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Parameter idPesanan tidak boleh kosong.").build();
        }

        OrderResponse order = orderService.getOrderById(idPesanan);
        return Response.ok(order).build();
    }


    @GET
    @Path("/noFaktur")
    public Response getOrderByNoFaktur(@QueryParam("noFaktur") String nomor) {
        if (nomor == null || nomor.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Parameter noFaktur tidak boleh kosong.").build();
        }

        OrderResponse order = orderService.getOrderBynoFaktur(nomor);
        return Response.ok(order).build();
    }


    @POST
    public Response createOrder(OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return Response.status(Response.Status.CREATED).entity(orderResponse).build();
    }


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

            double total = PriceOrder.hitungHargaTotal(tipe, jenis, request.getQty());
            return Response.ok(new HargaResponseBody(total)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/hapus")
    public Response deleteOrder(@QueryParam("idPesanan") String idPesanan) {
        if (idPesanan == null || idPesanan.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("idPesanan tidak boleh kosong").build();
        }
        try {
            orderService.deleteOrder(idPesanan);
            return Response.ok("Pesanan dengan ID " + idPesanan + " berhasil dihapus.").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Gagal menghapus pesanan").build();
        }
    }


    @PUT
    @Path("/soft-delete")
    public Response softDeleteOrder(@QueryParam("idPesanan") String idPesanan) {
        if (idPesanan == null || idPesanan.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("idPesanan tidak boleh kosong").build();
        }
        try {
            boolean isDeleted = orderService.softDeleteOrder(idPesanan);
            if (isDeleted) {
                return Response.ok("Pesanan dengan ID " + idPesanan + " berhasil dihapus").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Pesanan tidak ditemukan").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Gagal menghapus pesanan").build();
        }
    }


}
