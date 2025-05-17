package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.utils.StrukPdfGenerator;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;
import com.haylaundry.service.backend.modules.ordermanagement.repository.OrderRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

        String tanggalMasuk = order.getTglMasuk().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String tanggalSelesai = order.getTglSelesai() != null
                ? order.getTglSelesai().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : null;

        byte[] pdf = StrukPdfGenerator.generateStruk(
                order.getNoFaktur(),
                order.getCustomerName(),
                order.getQty() + " KG",
                String.valueOf(order.getHarga()),
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
