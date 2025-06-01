package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.utils.StrukOrderUnitGenerator;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.OrderUnitRepository;
import java.text.DecimalFormat;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/api/struk/unit")
public class OrderUnitResource {

    @Inject
    private OrderUnitRepository orderUnitRepository;

    @GET
    @Path("/{idPesanan}")
    @Produces("application/pdf")
    public Response downloadStrukOrderUnit(@PathParam("idPesanan") String idPesanan) {
        // Ambil order unit dari repository berdasarkan idPesanan
        var orderUnit = orderUnitRepository.getAllOrderUnit().stream()
                .filter(o -> o.getIdDetail().equals(idPesanan))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Pesanan satuan tidak ditemukan", 404));

        // Format tanggal masuk & selesai
        String tanggalMasuk = orderUnit.getTglMasuk().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String tanggalSelesai = orderUnit.getTglSelesai() != null
                ? orderUnit.getTglSelesai().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : "-";

        // Mengelompokkan DetailOrderUnitResponse berdasarkan kategori
        Map<String, List<StrukOrderUnitGenerator.Item>> kategoriItemMap = orderUnit.getItems().stream()
                .collect(Collectors.groupingBy(
                        OrderUnitResponse::getKategoriBarang,
                        Collectors.mapping(
                                item -> new StrukOrderUnitGenerator.Item(
                                        // Nama item diambil dari kategori + ukuran + jenis layanan, sesuaikan kebutuhan
                                        item.getKategoriBarang() + " " + item.getUkuran() + " " + item.getJenisLayanan(),
                                        item.getQty(),
                                        String.valueOf(item.getHarga())
                                ),
                                Collectors.toList()
                        )
                ));

        // Format totalHarga dengan pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedTotalHarga = String.valueOf(orderUnit.getTotalHarga());  // Format total harga dengan pemisah ribuan

        // Generate PDF dengan totalHarga yang sudah diformat
        byte[] pdf = StrukOrderUnitGenerator.generateStrukPesananSatuan(
                orderUnit.getNoFaktur(),
                orderUnit.getNamaCustomer(),
                tanggalMasuk,
                tanggalSelesai,
                orderUnit.getStatusBayar().toUpperCase(),
                orderUnit.getStatusOrder().toUpperCase(),
                kategoriItemMap,
                formattedTotalHarga  // Menggunakan total harga yang sudah diformat
        );

        return Response.ok(pdf)
                .header("Content-Disposition", "inline; filename=\"struk-" + orderUnit.getNoFaktur() + ".pdf\"")
                .build();
    }

}
