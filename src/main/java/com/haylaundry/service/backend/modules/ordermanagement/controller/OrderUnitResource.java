//package com.haylaundry.service.backend.modules.ordermanagement.controller;
//
//import com.haylaundry.service.backend.core.utils.PdfToImageConverter;
//import com.haylaundry.service.backend.core.utils.StrukOrderUnitGenerator;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.Response;
//
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Path("/api/struk/unit")
//public class OrderUnitResource {
//
//    @Inject
//    private OrderUnitRepository orderUnitRepository;
//
//    @GET
//    @Path("/{idPesanan}")
//    @Produces("image/png")
//    public Response downloadStrukOrderUnit(@PathParam("idPesanan") String idPesanan) throws IOException {
//        // Ambil order unit dari repository berdasarkan idPesanan
//        var orderUnit = orderUnitRepository.getAllOrderUnit().stream()
//                .filter(o -> o.getIdDetail().equals(idPesanan))
//                .findFirst()
//                .orElseThrow(() -> new WebApplicationException("Pesanan satuan tidak ditemukan", 404));
//
//        // Format tanggal masuk & selesai
//        String tanggalMasuk = orderUnit.getTglMasuk().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
//        String tanggalSelesai = orderUnit.getTglSelesai() != null
//                ? orderUnit.getTglSelesai().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
//                : "-";
//
//        Map<String, List<StrukOrderUnitGenerator.Item>> kategoriItemMap = orderUnit.getItems().stream()
//                .collect(Collectors.groupingBy(
//                        OrderUnitResponse::getKategoriBarang,
//                        Collectors.mapping(
//                                item -> new StrukOrderUnitGenerator.Item(
//                                        item.getKategoriBarang() + " " + item.getUkuran() + " " + item.getJenisLayanan(),
//                                        item.getQty(),
//                                        String.valueOf(item.getHarga())
//                                ),
//                                Collectors.toList()
//                        )
//                ));
//
//        DecimalFormat formatter = new DecimalFormat("#,###");
//        String formattedTotalHarga = String.valueOf(orderUnit.getTotalHarga());
//
//        byte[] pdf = StrukOrderUnitGenerator.generateStrukPesananSatuan(
//                orderUnit.getNoFaktur(),
//                orderUnit.getNamaCustomer(),
//                tanggalMasuk,
//                orderUnit.getStatusBayar().toUpperCase(),
//                orderUnit.getStatusOrder().toUpperCase(),
//                kategoriItemMap,
//                formattedTotalHarga
//        );
//
//        // 2. Convert PDF to PNG
//        byte[] imageBytes = PdfToImageConverter.convertPdfToImage(pdf);
//
//        return Response.ok(imageBytes)
//                .type("image/png")
//                .header("Content-Disposition", "inline; filename=\"struk-" + orderUnit.getNoFaktur() + ".png\"")
//                .build();
//    }
//
//
//
//}
