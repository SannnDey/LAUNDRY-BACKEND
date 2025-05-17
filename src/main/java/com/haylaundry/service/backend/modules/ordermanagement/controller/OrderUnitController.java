package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.exception.ErrorResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.DetailOrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.OrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.DetailOrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.service.OrderUnitService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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

    @POST
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

}
