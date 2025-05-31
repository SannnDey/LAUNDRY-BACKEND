package com.haylaundry.service.backend.modules.report.controller;

import com.haylaundry.service.backend.modules.report.models.finance.response.FinanceDateResponse;
import com.haylaundry.service.backend.modules.report.models.finance.response.FinanceResponse;
import com.haylaundry.service.backend.modules.report.service.FinanceService;
import com.haylaundry.service.backend.jooq.gen.tables.records.LaporanKeuanganRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/laporan-keuangan")
public class FinanceController {

    @Inject
    private FinanceService financeService;

    /**
     * Endpoint untuk mengambil laporan keuangan bulanan berdasarkan bulan dan tahun
     *
     * @param date Bulan dan tahun laporan yang ingin diambil (format: 'MMMM yyyy', misalnya 'Mei 2025')
     * @return Response dengan status sukses atau gagal
     */
    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyIncomeReport(@QueryParam("date") String date) {
        try {
            // Memanggil service untuk mengambil laporan keuangan bulanan
            FinanceResponse response = financeService.getLaporanKeuanganByMonth(date);

            // Mengembalikan response dengan status 200 OK
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            // Mengembalikan response dengan status BAD_REQUEST jika bulan atau format tidak valid
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            // Mengembalikan response dengan status INTERNAL_SERVER_ERROR untuk kesalahan yang tidak terduga
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal Server Error: " + e.getMessage())
                    .build();
        }
    }


    // Endpoint to get financial report by date range
    @GET
    @Path("/byDateRange")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportByDateRange(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
        try {
            FinanceDateResponse response = financeService.getReportByDateRange(startDate, endDate);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

}
