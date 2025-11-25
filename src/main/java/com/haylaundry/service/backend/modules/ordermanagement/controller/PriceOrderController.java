package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PriceOrderSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.price.request.PriceOrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.price.response.PriceOrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.service.PriceOrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/price-order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PriceOrderController {

    @Inject
    private PriceOrderService priceOrderService;

    // ✅ Ambil semua harga
    @GET
    public Response getAllPrices() {
        List<PriceOrderResponse> prices = priceOrderService.getAllPrices();
        return Response.ok(prices).build();
    }

    // ✅ Ambil harga berdasarkan tipe & jenis cucian
    @GET
    @Path("/find")
    public Response getHargaByTipeAndJenis(@QueryParam("tipe") String tipeCucian,
                                           @QueryParam("jenis") String jenisCucian) {
        PriceOrderResponse response = priceOrderService.getHargaByTipeAndJenis(tipeCucian, jenisCucian);
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Data harga tidak ditemukan untuk tipe dan jenis cucian yang diberikan.")
                    .build();
        }
        return Response.ok(response).build();
    }

    // ✅ Tambah atau update harga
    @POST
    public Response createOrUpdateHarga(PriceOrderRequest request) {
        priceOrderService.createOrUpdateHarga(request);
        return Response.ok("Harga berhasil ditambahkan atau diperbarui.").build();
    }


    @PUT
    @Path("/update-harga")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHargaById(
            @QueryParam("idPrice") String idPrice,
            @QueryParam("hargaPerKg") Double hargaPerKg) {

        if (idPrice == null || idPrice.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Query param 'idPrice' harus diisi.")
                    .build();
        }
        if (hargaPerKg == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Query param 'hargaPerKg' harus diisi.")
                    .build();
        }

        try {
            priceOrderService.updateHargaById(idPrice, hargaPerKg);
            return Response.ok("Harga berhasil diperbarui.").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Gagal memperbarui harga.")
                    .build();
        }
    }

    // Hard delete price by ID
    @DELETE
    @Path("/{id}")
    public Response deletePriceById(@PathParam("id") String id) {
        boolean deleted = priceOrderService.deleteHargaById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Price with ID " + id + " not found.")
                    .build();
        }
    }
}
