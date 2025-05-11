package com.haylaundry.service.backend.masterdata.customer.controller;

import com.haylaundry.service.backend.masterdata.customer.models.request.CustomerRequestBody;
import com.haylaundry.service.backend.masterdata.customer.models.response.CustomerResponseBody;
import com.haylaundry.service.backend.masterdata.customer.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/customer")
public class CustomerController {

    @Inject
    private CustomerService customerService;

    // Endpoint untuk mendapatkan semua customer
    @GET
    public Response getAllCustomers() {
        List<CustomerResponseBody> customers = customerService.getAllCustomers();
        return Response.ok(customers).build();
    }

    // Endpoint untuk mencari customer berdasarkan nomor telepon
    @GET
    @Path("/telp/{noTelp}")
    public Response getCustomerByNoTelp(@PathParam("noTelp") String noTelp) {
        try {
            CustomerResponseBody customer = customerService.findByNoTelp(noTelp);
            return Response.ok(customer).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    // Endpoint untuk membuat customer baru atau mengambil customer yang sudah ada berdasarkan noTelp
    @POST
    public Response createOrGetCustomer(CustomerRequestBody customerRequestBody) {
        // Mengecek apakah customer sudah ada berdasarkan noTelp
        CustomerResponseBody customer = customerService.createOrGet(customerRequestBody);

        // Menambahkan logika pengecekan apakah customer baru dibuat atau sudah ada
        if (customer.getCreatedAt() != null) {
            // Jika ada customer baru yang dibuat
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            // Jika customer sudah ada
            return Response.status(Response.Status.OK).entity(customer).build();
        }
    }
}
