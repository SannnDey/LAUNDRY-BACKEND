package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.utils.StrukOrderGenerator;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;
import com.haylaundry.service.backend.modules.ordermanagement.repository.OrderRepository;
import java.text.DecimalFormat;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.format.DateTimeFormatter;

@Path("/api/struk")
public class OrderResource {

    @Inject
    private OrderRepository orderRepository;


    @GET
    @Path("/{idPesanan}")
    @Produces("application/pdf")
    public Response downloadStruk(@PathParam("idPesanan") String idPesanan) {
        var order = orderRepository.getAll().stream()
                .filter(o -> o.getIdPesanan().equals(idPesanan))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Pesanan tidak ditemukan", 404));

        // Format tanggalMasuk dan tanggalSelesai
        String tanggalMasuk = order.getTglMasuk().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String tanggalSelesai = order.getTglSelesai() != null
                ? order.getTglSelesai().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : null;

        // Format harga dengan pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedHarga = String.valueOf(order.getHarga());  // Format harga dengan pemisah ribuan

        // Generate PDF dengan harga yang sudah diformat
        byte[] pdf = StrukOrderGenerator.generateStruk(
                order.getNoFaktur(),
                order.getCustomerName(),
                order.getQty() + " KG",
                formattedHarga,  // Menggunakan harga yang sudah diformat
                tanggalMasuk,
                tanggalSelesai,
                PesananStatusBayar.valueOf(order.getStatusBayar()).getLiteral(),
                PesananStatusOrder.valueOf(order.getStatusOrder()).getLiteral(),
                PesananTipeCucian.valueOf(order.getTipeCucian()).getLiteral()
        );

        return Response.ok(pdf)
                .header("Content-Disposition", "inline; filename=\"struk-" + order.getNoFaktur() + ".pdf\"")
                .build();
    }


}
