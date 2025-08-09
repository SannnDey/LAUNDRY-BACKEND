package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.utils.PdfToImageConverter;
import com.haylaundry.service.backend.core.utils.StrukOrderGenerator;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;
import com.haylaundry.service.backend.modules.ordermanagement.repository.OrderRepository;

import java.io.IOException;
import java.text.DecimalFormat;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/api/struk")
public class OrderResource {

    @Inject
    private OrderRepository orderRepository;


    @GET
    @Path("/{idPesanan}")
    @Produces("image/png")
    public Response downloadStruk(@PathParam("idPesanan") String idPesanan) throws IOException {
        var order = orderRepository.getAll().stream()
                .filter(o -> o.getIdPesanan().equals(idPesanan))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Pesanan tidak ditemukan", 404));

        // Formatter sesuai format data dari database / model
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

// Formatter untuk format tampilan
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

// Parse string ke LocalDateTime
        LocalDateTime tglMasuk = LocalDateTime.parse(order.getTglMasuk(), parser);
        String tanggalMasuk = tglMasuk.format(displayFormatter);

// Parse dan format untuk tglSelesai (cek null)
        String tanggalSelesai = null;
        if (order.getTglSelesai() != null && !order.getTglSelesai().isEmpty()) {
            LocalDateTime tglSelesaiParsed = LocalDateTime.parse(order.getTglSelesai(), parser);
            tanggalSelesai = tglSelesaiParsed.format(displayFormatter);
        }


        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedHarga = String.valueOf(order.getHarga());

        byte[] pdf = StrukOrderGenerator.generateStruk(
                order.getNoFaktur(),
                order.getCustomerName(),
                order.getQty() + " KG",
                formattedHarga,
                tanggalMasuk,
                PesananStatusBayar.valueOf(order.getStatusBayar()).getLiteral(),
                PesananStatusOrder.valueOf(order.getStatusOrder()).getLiteral(),
                PesananTipeCucian.valueOf(order.getTipeCucian()).getLiteral()
        );

        // 2. Convert PDF to PNG
        byte[] imageBytes = PdfToImageConverter.convertPdfToImage(pdf);


        return Response.ok(imageBytes)
                .type("image/png")
                .header("Content-Disposition", "inline; filename=\"struk-" + order.getNoFaktur() + ".png\"")
                .build();

    }


}
