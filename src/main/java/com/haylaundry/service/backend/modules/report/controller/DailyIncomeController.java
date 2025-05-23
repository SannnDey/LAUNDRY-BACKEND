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
            // Memanggil service untuk membuat laporan dengan tanggal yang diberikan
            dailyIncomeService.createLaporan(tanggal);
            return Response.ok("Laporan pemasukan harian berhasil dibuat untuk: " + tanggal).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Terjadi kesalahan saat membuat laporan: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint untuk mengambil laporan pemasukan harian berdasarkan tanggal
     *
     * @param tanggal Tanggal laporan yang ingin diambil
     * @return Response dengan data laporan pemasukan harian
     */
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDailyReport(@QueryParam("tanggal") String tanggal) {
        try {
            // Mengonversi tanggal dari String ke LocalDate
            LocalDate tglReport = LocalDate.parse(tanggal);

            // Memanggil service untuk mendapatkan laporan berdasarkan tanggal
            DailyIncomeResponse laporan = dailyIncomeService.getLaporanByDate(tglReport);
            return Response.ok(laporan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Terjadi kesalahan saat mengambil laporan: " + e.getMessage()).build();
        }
    }
}
