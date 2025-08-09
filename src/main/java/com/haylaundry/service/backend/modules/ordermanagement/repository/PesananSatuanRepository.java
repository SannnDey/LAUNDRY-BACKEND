package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.core.utils.InvoiceGenerator;
import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.PesananSatuanStatusBayar;
import com.haylaundry.service.backend.jooq.gen.enums.PesananSatuanStatusOrder;
import com.haylaundry.service.backend.jooq.gen.enums.PesananSatuanTipePembayaran;
import com.haylaundry.service.backend.jooq.gen.tables.records.CustomerRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.ItemPesananSatuanRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.PesananSatuanRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.ItemPesananSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PesananSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.*;
import com.haylaundry.service.backend.modules.report.service.DailyIncomeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.sql.Timestamp;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.util.stream.Collectors;


@ApplicationScoped
public class PesananSatuanRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;
    @Inject
    ItemPesananSatuanRepository itemPesananSatuanRepository;
    @Inject
    PriceOrderSatuanRepository priceOrderSatuanRepository;
    @Inject
    private DailyIncomeService dailyIncomeService;

    public PesananSatuanResponse getPesananSatuanByNoFaktur(String nomor) {
        String noFaktur = "INV-" + nomor;

        // Ambil data pesanan dan join dengan customer
        Record pesananRecord = jooq.select()
                .from(Tables.PESANAN_SATUAN)
                .join(Tables.CUSTOMER).on(Tables.PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.PESANAN_SATUAN.NO_FAKTUR.eq(noFaktur))
                .fetchOne();

        if (pesananRecord == null) {
            return null; // atau lempar exception jika perlu
        }

        // Formatter harga (pemisah ribuan titik)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        // Formatter tanggal
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        PesananSatuanResponse response = new PesananSatuanResponse();
        response.setIdPesananSatuan(pesananRecord.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN));
        response.setIdCustomer(pesananRecord.get(Tables.PESANAN_SATUAN.ID_CUSTOMER));
        response.setNoFaktur(pesananRecord.get(Tables.PESANAN_SATUAN.NO_FAKTUR));
        response.setNamaCustomer(pesananRecord.get(Tables.CUSTOMER.NAMA));
        response.setCustomerPhone(pesananRecord.get(Tables.CUSTOMER.NO_TELP));
        response.setTipePembayaran(String.valueOf(pesananRecord.get(Tables.PESANAN_SATUAN.TIPE_PEMBAYARAN)));

        PesananSatuanStatusBayar statusBayar = pesananRecord.get(Tables.PESANAN_SATUAN.STATUS_BAYAR);
        response.setStatusBayar(statusBayar != null ? statusBayar.getLiteral() : null);

        PesananSatuanStatusOrder statusOrder = pesananRecord.get(Tables.PESANAN_SATUAN.STATUS_ORDER);
        response.setStatusOrder(statusOrder != null ? statusOrder.getLiteral() : null);

        Double totalHarga = pesananRecord.get(Tables.PESANAN_SATUAN.TOTAL_HARGA);
        response.setTotalHarga(formatter.format(totalHarga));

        // Format tanggal masuk & selesai
        LocalDateTime tglMasuk = pesananRecord.get(Tables.PESANAN_SATUAN.TGL_MASUK);
        LocalDateTime tglSelesai = pesananRecord.get(Tables.PESANAN_SATUAN.TGL_SELESAI);

        response.setTglMasuk(tglMasuk != null ? tglMasuk.format(dateFormatter) : null);
        response.setTglSelesai(tglSelesai != null ? tglSelesai.format(dateFormatter) : null);

        response.setCatatan(pesananRecord.get(Tables.PESANAN_SATUAN.CATATAN));

        // Ambil item dari ITEM_PESANAN_SATUAN dan gabungkan dengan PRICE_ORDER_SATUAN
        List<ItemPesananSatuanResponse> items = jooq.select(
                        Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN,
                        Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN,
                        Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN,
                        Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG,
                        Tables.PRICE_ORDER_SATUAN.UKURAN,
                        Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN,
                        Tables.ITEM_PESANAN_SATUAN.QTY
                )
                .from(Tables.ITEM_PESANAN_SATUAN)
                .join(Tables.PRICE_ORDER_SATUAN)
                .on(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN.eq(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN))
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(
                        pesananRecord.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN)
                ))
                .fetchInto(ItemPesananSatuanResponse.class);

        response.setItems(items);

        return response;
    }



    public List<PesananSatuanResponse> getAllPesananSatuan() {
        // Formatter harga pakai titik
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        // Formatter tanggal
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Ambil semua pesanan dan join ke customer
        List<Record> records = jooq.select()
                .from(Tables.PESANAN_SATUAN)
                .join(Tables.CUSTOMER).on(Tables.PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .fetch();

        List<PesananSatuanResponse> responses = new ArrayList<>();

        for (Record pesananRecord : records) {
            PesananSatuanResponse response = new PesananSatuanResponse();
            String idPesanan = pesananRecord.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN);

            response.setIdPesananSatuan(idPesanan);
            response.setIdCustomer(pesananRecord.get(Tables.PESANAN_SATUAN.ID_CUSTOMER));
            response.setNoFaktur(pesananRecord.get(Tables.PESANAN_SATUAN.NO_FAKTUR));
            response.setNamaCustomer(pesananRecord.get(Tables.CUSTOMER.NAMA));
            response.setCustomerPhone(pesananRecord.get(Tables.CUSTOMER.NO_TELP));
            response.setTipePembayaran(String.valueOf(pesananRecord.get(Tables.PESANAN_SATUAN.TIPE_PEMBAYARAN)));

            PesananSatuanStatusBayar statusBayar = pesananRecord.get(Tables.PESANAN_SATUAN.STATUS_BAYAR);
            response.setStatusBayar(statusBayar != null ? statusBayar.getLiteral() : null);

            PesananSatuanStatusOrder statusOrder = pesananRecord.get(Tables.PESANAN_SATUAN.STATUS_ORDER);
            response.setStatusOrder(statusOrder != null ? statusOrder.getLiteral() : null);

            Double totalHarga = pesananRecord.get(Tables.PESANAN_SATUAN.TOTAL_HARGA);
            response.setTotalHarga(formatter.format(totalHarga));

            // Format tanggal masuk & selesai
            LocalDateTime tglMasuk = pesananRecord.get(Tables.PESANAN_SATUAN.TGL_MASUK);
            LocalDateTime tglSelesai = pesananRecord.get(Tables.PESANAN_SATUAN.TGL_SELESAI);

            response.setTglMasuk(tglMasuk != null ? tglMasuk.format(dateFormatter) : null);
            response.setTglSelesai(tglSelesai != null ? tglSelesai.format(dateFormatter) : null);

            response.setCatatan(pesananRecord.get(Tables.PESANAN_SATUAN.CATATAN));

            // Ambil item untuk setiap pesanan satuan
            List<ItemPesananSatuanResponse> items = jooq.select(
                            Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN,
                            Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN,
                            Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN,
                            Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG,
                            Tables.PRICE_ORDER_SATUAN.UKURAN,
                            Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN,
                            Tables.ITEM_PESANAN_SATUAN.QTY
                    )
                    .from(Tables.ITEM_PESANAN_SATUAN)
                    .join(Tables.PRICE_ORDER_SATUAN)
                    .on(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN.eq(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN))
                    .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesanan))
                    .fetchInto(ItemPesananSatuanResponse.class);

            response.setItems(items);

            responses.add(response);
        }

        return responses;
    }





    public void createPesananSatuan(PesananSatuanRequest request) {
        List<ItemPesananSatuanRequest> itemRequests = request.getIdItemList();
        List<ItemPesananSatuanRecord> items = new ArrayList<>();

        double totalHarga = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (ItemPesananSatuanRequest itemReq : itemRequests) {
            PriceOrderSatuanResponse price = priceOrderSatuanRepository
                    .getPriceById(itemReq.getIdPriceSatuan())
                    .orElseThrow(() -> new RuntimeException("Data harga tidak ditemukan untuk ID: " + itemReq.getIdPriceSatuan()));

            double hargaSatuan = price.getHargaPerItem();
            int qty = itemReq.getQty();
            double subtotal = hargaSatuan * qty;

            String formattedHarga = decimalFormat.format(hargaSatuan);
            String formattedSubtotal = decimalFormat.format(subtotal);

            System.out.println("Item: " + itemReq.getIdPriceSatuan() + ", Qty: " + qty + ", Harga: Rp" + formattedHarga + ", Subtotal: Rp" + formattedSubtotal);

            ItemPesananSatuanRecord itemRecord = jooq.newRecord(Tables.ITEM_PESANAN_SATUAN);
            itemRecord.setIdItemSatuan(UuidCreator.getTimeOrderedEpoch().toString());
            itemRecord.setIdPriceSatuan(itemReq.getIdPriceSatuan());
            itemRecord.setQty(qty);

            items.add(itemRecord);
            totalHarga += subtotal;
        }

        String formattedTotal = decimalFormat.format(totalHarga);
        System.out.println("Total Harga Pesanan: Rp" + formattedTotal);

        String idPesanan = UuidCreator.getTimeOrderedEpoch().toString();
        String noFaktur = InvoiceGenerator.generateNoFaktur();

        ZoneId witaZone = ZoneId.of("Asia/Makassar");
        LocalDateTime tglMasuk = ZonedDateTime.now(witaZone).toLocalDateTime();

        PesananSatuanTipePembayaran tipePembayaranEnum = PesananSatuanTipePembayaran.valueOf(request.getTipePembayaran());
        PesananSatuanStatusBayar statusBayarEnum = PesananSatuanStatusBayar.valueOf(request.getStatusBayar());
        PesananSatuanStatusOrder statusOrderEnum = PesananSatuanStatusOrder.valueOf(request.getStatusOrder());

        jooq.insertInto(Tables.PESANAN_SATUAN)
                .set(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN, idPesanan)
                .set(Tables.PESANAN_SATUAN.ID_CUSTOMER, request.getIdCustomer())
                .set(Tables.PESANAN_SATUAN.NO_FAKTUR, noFaktur)
                .set(Tables.PESANAN_SATUAN.TIPE_PEMBAYARAN, tipePembayaranEnum)
                .set(Tables.PESANAN_SATUAN.STATUS_BAYAR, statusBayarEnum)
                .set(Tables.PESANAN_SATUAN.STATUS_ORDER, statusOrderEnum)
                .set(Tables.PESANAN_SATUAN.TGL_MASUK, tglMasuk)
                .set(Tables.PESANAN_SATUAN.CATATAN, request.getCatatan())
                .set(Tables.PESANAN_SATUAN.TOTAL_HARGA, totalHarga)
                .execute();

        for (ItemPesananSatuanRecord item : items) {
            jooq.insertInto(Tables.ITEM_PESANAN_SATUAN)
                    .set(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN, item.getIdItemSatuan())
                    .set(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN, idPesanan)
                    .set(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN, item.getIdPriceSatuan())
                    .set(Tables.ITEM_PESANAN_SATUAN.QTY, item.getQty())
                    .execute();
        }

        // 🔥 Tambahkan ke laporan harian jika status bayar Lunas atau Belum_Lunas
        if (statusBayarEnum == PesananSatuanStatusBayar.Lunas ||
                statusBayarEnum == PesananSatuanStatusBayar.Belum_Lunas) {
            dailyIncomeService.createLaporan(tglMasuk.toLocalDate());
        }
    }




    // Soft delete untuk order unit
    public boolean softDeleteOrderUnitById(String idDetail) {
        // Soft delete pada item anak (pesanan_satuan)
        int updatedItems = jooq.update(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idDetail))
                .execute();

        // Soft delete pada parent (detail_pesanan_satuan)
        int updatedParent = jooq.update(Tables.PESANAN_SATUAN)
                .set(Tables.PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idDetail))
                .execute();

        return updatedItems > 0 && updatedParent > 0;
    }


    // Fungsi untuk memperbarui status bayar pesanan satuan
    public OrderUnitStatusBayar updateStatusBayar(String idDetail, String statusBayar) {
        // Ambil detail pesanan satuan berdasarkan idDetail
        PesananSatuanRecord orderUnitToUpdate = jooq.selectFrom(Tables.PESANAN_SATUAN)
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idDetail))
                .fetchOne();

        if (orderUnitToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        // Gunakan lookupLiteral untuk mencari enum dari string literal
        PesananSatuanStatusBayar status = PesananSatuanStatusBayar.lookupLiteral(statusBayar);

        if (status == null) {
            throw new IllegalArgumentException("Status bayar tidak valid: " + statusBayar);
        }

        // Update status bayar
        orderUnitToUpdate.setStatusBayar(status);
        orderUnitToUpdate.store();

        // Jika status bayar "Lunas", update laporan pemasukan
        if (PesananSatuanStatusBayar.Lunas.equals(status)) {
            // Memperbarui laporan pemasukan harian berdasarkan tanggal pesanan satuan
            dailyIncomeService.createLaporan(orderUnitToUpdate.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        } else if (PesananSatuanStatusBayar.Belum_Lunas.equals(status)) {
            // Memperbarui laporan piutang harian jika belum lunas
            dailyIncomeService.createLaporan(orderUnitToUpdate.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        }

        return new OrderUnitStatusBayar(
                orderUnitToUpdate.getIdPesananSatuan(),
                orderUnitToUpdate.getNoFaktur(),
                status.getLiteral() // Pastikan ini yang dikirim ke FE
        );
    }

    // Fungsi untuk memperbarui status order pesanan satuan
    public OrderUnitStatusResponse updateStatusOrder(String idDetail, String statusOrder) {
        PesananSatuanRecord orderUnitTopUpdate = jooq.selectFrom(Tables.PESANAN_SATUAN)
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idDetail))
                .fetchOne();

        if (orderUnitTopUpdate == null ) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        PesananSatuanStatusOrder status = PesananSatuanStatusOrder.lookupLiteral(statusOrder);
        orderUnitTopUpdate.setStatusOrder(status);

        if (PesananSatuanStatusOrder.Selesai.equals(status)) {
            orderUnitTopUpdate.setTglSelesai(LocalDateTime.now());
        }

        orderUnitTopUpdate.setStatusOrder(status);
        orderUnitTopUpdate.store();

        return new OrderUnitStatusResponse(
                orderUnitTopUpdate.getIdPesananSatuan(),
                orderUnitTopUpdate.getNoFaktur(),
                orderUnitTopUpdate.getStatusBayar().toString(),
                status.getLiteral(),
                orderUnitTopUpdate.getTglSelesai()
        );
    }

}