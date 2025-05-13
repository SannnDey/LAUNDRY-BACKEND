package com.haylaundry.service.backend.modules.customer.controller;

import com.haylaundry.service.backend.modules.customer.models.request.CustomerRequestBody;
import com.haylaundry.service.backend.modules.customer.models.response.CustomerResponseBody;
import com.haylaundry.service.backend.modules.customer.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/customer")
public class CustomerController {

    @Inject
    private CustomerService customerService;

    // GET all customers
    @GET
    public Response getAllCustomers(@QueryParam("noTelp") String noTelp) {
        if (noTelp != null && !noTelp.isEmpty()) {
            try {
                CustomerResponseBody customer = customerService.findByNoTelp(noTelp);
                return Response.ok(customer).build();
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
        } else {
            List<CustomerResponseBody> customers = customerService.getAllCustomers();
            return Response.ok(customers).build();
        }
    }

    // POST create or get customer
    @POST
    public Response createOrGetCustomer(CustomerRequestBody customerRequestBody) {
        CustomerResponseBody customer = customerService.createOrGet(customerRequestBody);

        if (customer.getCreatedAt() != null) {
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            return Response.status(Response.Status.OK).entity(customer).build();
        }
    }
}
