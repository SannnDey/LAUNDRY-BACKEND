package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PesananSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PriceOrderSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.PesananSatuanResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.PriceOrderSatuanResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.PesananSatuanRepository;
import com.haylaundry.service.backend.modules.ordermanagement.service.PesananSatuanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/pesanan-satuan")
public class PesananSatuanController {

    @Inject
    PesananSatuanService pesananService;

    @Inject
    PesananSatuanRepository pesananRepo;


    // ✅ GET ALL ORDER UNIT
    @GET
    @Path("/all")
    public Response getAllOrderUnits() {
        List<PesananSatuanResponse> result = pesananService.getAllOrderUnit();
        return Response.ok(result).build();
    }

    @GET
    @Path("/price")
    public Response getAllPriceOrder(){
        List<PriceOrderSatuanResponse> result = pesananService.getAllPrice();
        return Response.ok(result).build();
    }

    // ✅ GET ORDER UNIT BY NO FAKTUR (e.g. ?noFaktur=0001)
    @GET
    @Path("/noFaktur")
    public Response getOrderUnitByNoFaktur(@QueryParam("noFaktur") String noFaktur) {
        if (noFaktur == null || noFaktur.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Query param 'noFaktur' harus diisi.")
                    .build();
        }

        PesananSatuanResponse result = pesananService.getOrderUnitByNoFaktur(noFaktur);
        return Response.ok(result).build();
    }


    @POST
    @Path("/price")
    public Response tambahItemSatuan(PriceOrderSatuanRequest request) {
        try {
            pesananService.createPrice(request);
            return Response.ok("Item berhasil ditambahkan.").build();
        } catch (Exception e) {
            e.printStackTrace(); // untuk debug
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Gagal menambahkan item: " + e.getMessage())
                    .build();
        }
    }

    @POST
    public Response createPesananSatuan(PesananSatuanRequest request) {
        pesananService.createPesananSatuan(request);
        return Response.ok("Pesanan satuan berhasil dibuat").build();
    }

    @PUT
    @Path("/update-status")
    public Response updateStatusBayar(
            @QueryParam("idDetail") String idDetail,
            @QueryParam("statusBayar") String statusBayar) {
        try {
            OrderUnitStatusBayar response = pesananService.updateStatusBayar(idDetail, statusBayar);
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
            OrderUnitStatusResponse response = pesananService.updateStatusOrderUnit(idDetail, statusOrder);
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


    @PUT
    @Path("/soft-delete")
    public Response softDeleteOrderUnit(@QueryParam("idDetail") String idDetail) {
        if (idDetail == null || idDetail.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("idDetail tidak boleh kosong").build();
        }

        boolean isDeleted = pesananService.softDeleteOrderUnitById(idDetail);
        if (isDeleted) {
            return Response.ok("Order unit dengan ID " + idDetail + " berhasil dihapus").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Order unit tidak ditemukan").build();
        }
    }

    @PUT
    @Path("/price")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePrice(PriceOrderSatuanRequest request) {
        try {
            String idPriceSatuan = request.getIdPriceSatuan();
            if (idPriceSatuan == null || idPriceSatuan.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Field 'idPriceSatuan' harus diisi di body JSON.")
                        .build();
            }
            pesananService.updatePrice(idPriceSatuan, request);
            return Response.ok("Data harga berhasil diperbarui.").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Gagal memperbarui data harga.")
                    .build();
        }
    }


}
