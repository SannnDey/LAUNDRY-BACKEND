package com.haylaundry.service.backend.modules.report.controller;

import com.haylaundry.service.backend.modules.report.service.DailyIncomeService;
import com.haylaundry.service.backend.modules.report.models.dailyincome.response.DailyIncomeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

@ApplicationScoped
@Path("/laporan-pemasukan")
public class DailyIncomeController {

    @Inject
    private DailyIncomeService dailyIncomeService;

    /**
     * Endpoint untuk menghasilkan laporan pemasukan harian berdasarkan tanggal
     *
     * @param tanggal Tanggal laporan yang ingin dihasilkan
     * @return Response dengan status sukses atau gagal
     */
    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateDailyReport(LocalDate tanggal) {
        try {
            dailyIncomeService.createLaporan(tanggal);
            return Response.ok("Laporan pemasukan harian berhasil dibuat untuk: " + tanggal).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Terjadi kesalahan saat membuat laporan: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/report")
    public Response getDailyIncomeReport(@QueryParam("date") String dateString) {
        try {
            DailyIncomeResponse response = dailyIncomeService.getLaporanByDate(dateString);

            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal Server Error: " + e.getMessage())
                    .build();
        }
    }
}
