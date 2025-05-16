package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.exception.ErrorResponse;
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

@Path("/api/order-unit")
public class OrderUnitController {
    @Inject
    private OrderUnitService orderUnitService;

    @GET
    public Response getAllOrderUnits() {
        List<DetailOrderUnitResponse> allOrders = orderUnitService.getAllOrderUnits();
        return Response.ok(allOrders).build();
    }

    @POST
    public Response createOrderUnit(OrderUnitRequest request) {
        try {
            OrderUnitResponse createdOrder = orderUnitService.createOrderUnit(request);
            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Terjadi kesalahan pada server"))
                    .build();
        }
    }
}
