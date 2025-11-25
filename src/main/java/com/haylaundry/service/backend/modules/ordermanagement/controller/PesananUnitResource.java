package com.haylaundry.service.backend.modules.ordermanagement.controller;

import com.haylaundry.service.backend.core.utils.PdfToImageConverter;
import com.haylaundry.service.backend.core.utils.StrukOrderUnitGenerator;
import com.haylaundry.service.backend.core.utils.StrukOrderUnitGenerator.Item;
import com.haylaundry.service.backend.jooq.gen.Tables;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Record5;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Path("/pesanan-unit")
public class PesananUnitResource {

    @Inject
    DSLContext jooq;

    @GET
    @Path("/{idPesananSatuan}")
    @Produces("image/png")
    public Response downloadStrukOrderUnit(@PathParam("idPesananSatuan") String idPesananSatuan) {
        // Ambil data pesanan dari database
        Record pesanan = jooq.select()
                .from(Tables.PESANAN_SATUAN)
                .join(Tables.CUSTOMER)
                .on(Tables.PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesananSatuan))
                .fetchOne();

        if (pesanan == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pesanan satuan tidak ditemukan")
                    .build();
        }

        // Formatter harga (titik sebagai pemisah ribuan)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        // Ambil field utama
        String noFaktur = pesanan.get(Tables.PESANAN_SATUAN.NO_FAKTUR);
        String namaCustomer = pesanan.get(Tables.CUSTOMER.NAMA);

        // Format tanggal masuk
        LocalDateTime tglMasuk = pesanan.get(Tables.PESANAN_SATUAN.TGL_MASUK);
        String tanggalMasuk = tglMasuk.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        String statusBayar = pesanan.get(Tables.PESANAN_SATUAN.STATUS_BAYAR).getLiteral();
        String statusOrder = pesanan.get(Tables.PESANAN_SATUAN.STATUS_ORDER).getLiteral();

        Double total = pesanan.get(Tables.PESANAN_SATUAN.TOTAL_HARGA);
        String totalHarga = decimalFormat.format(total);

        // Ambil item pesanan
        Result<Record5<Integer, String, String, String, Double>> items = jooq.select(
                        Tables.ITEM_PESANAN_SATUAN.QTY,
                        Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG,
                        Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN,
                        Tables.PRICE_ORDER_SATUAN.UKURAN,
                        Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM
                )
                .from(Tables.ITEM_PESANAN_SATUAN)
                .join(Tables.PRICE_ORDER_SATUAN)
                .on(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN.eq(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN))
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesananSatuan))
                .fetch();

        // Kelompokkan berdasarkan kategori barang
        Map<String, List<Item>> kategoriItemMap = new LinkedHashMap<>();
        for (Record record : items) {
            String kategori = record.get(Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG);
            String layanan = record.get(Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN);
            String ukuran = record.get(Tables.PRICE_ORDER_SATUAN.UKURAN);
            Integer qty = record.get(Tables.ITEM_PESANAN_SATUAN.QTY);
            Double hargaSatuan = record.get(Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM);

            // Hitung total harga per item
            Double hargaTotalPerItem = hargaSatuan * qty;
            String hargaFormatted = decimalFormat.format(hargaTotalPerItem);

            // Nama item
            String namaItem = kategori + " - " + ukuran + " - " + layanan;

            Item item = new Item(namaItem, qty, hargaFormatted);

            kategoriItemMap.computeIfAbsent(kategori, k -> new ArrayList<>()).add(item);
        }

        try {
            // Generate PDF
            byte[] pdfBytes = StrukOrderUnitGenerator.generateStrukPesananSatuan(
                    noFaktur,
                    namaCustomer,
                    tanggalMasuk,
                    statusBayar,
                    statusOrder,
                    kategoriItemMap,
                    totalHarga
            );

            // Convert PDF ke PNG
            byte[] imageBytes = PdfToImageConverter.convertPdfToImage(pdfBytes);

            // Return PNG
            return Response.ok(imageBytes)
                    .type("image/png")
                    .header("Content-Disposition", "inline; filename=\"struk-" + noFaktur + ".png\"")
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Gagal membuat struk: " + e.getMessage())
                    .build();
        }
    }

}
