package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.DetailOrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.DetailOrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusResponse;
import com.haylaundry.service.backend.modules.ordermanagement.service.OrderUnitService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@Path("/api/order-unit")
public class OrderUnitController {
    @Inject
    private OrderUnitService orderUnitService;
    private static final Logger logger = Logger.getLogger(OrderUnitController.class.getName());

    @GET
    public Response getAllOrderUnits() {
        List<DetailOrderUnitResponse> allOrders = orderUnitService.getAllOrderUnits();
        return Response.ok(allOrders).build();
    }

    @GET
    @Path("/detail")
    public Response getOrderUnitById(@QueryParam("idDetail") String idDetail) {
        DetailOrderUnitResponse orderUnit = orderUnitService.getOrderUnitById(idDetail);
        return Response.ok(orderUnit).build();
    }

    @POST
    @Path("/create")
    public Response createOrderUnit(DetailOrderUnitRequest request) {
        try {
            DetailOrderUnitResponse createdOrder = orderUnitService.createOrderUnit(request);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            logger.error("Gagal membuat order unit", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Terjadi kesalahan: " + e.getMessage()))
                    .build();
        }

    }

    // Versi menggunakan query param
    @PUT
    @Path("/update-status")
    public Response updateStatusBayar(
            @QueryParam("idDetail") String idDetail,
            @QueryParam("statusBayar") String statusBayar) {
        try {
            OrderUnitStatusBayar response = orderUnitService.updateStatusBayar(idDetail, statusBayar);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Terjadi kesalahan.").build();
        }
    }


    @PUT
    @Path("/update-status-order")
    public Response updateStatusOrderUnit(@QueryParam("idDetail") String idDetail,
                                          @QueryParam("statusOrder") String statusOrder) {
        try {
            OrderUnitStatusResponse response = orderUnitService.updateStatusOrderUnit(idDetail, statusOrder);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Terjadi kesalahan saat memperbarui status pesanan.")
                    .build();
        }
    }

    @DELETE
    @Path("/delete")
    public Response deleteOrderUnitById(@QueryParam("idDetail") String idDetail) {
        boolean isDeleted = orderUnitService.deleteOrderUnitById(idDetail);
        if (isDeleted) {
            return Response.ok(Map.of("message", "Order unit dengan ID " + idDetail + " berhasil dihapus.")).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Order unit dengan ID " + idDetail + " tidak ditemukan."))
                    .build();
        }
    }


}
