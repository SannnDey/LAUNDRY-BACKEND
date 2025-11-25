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
import java.util.Collections;
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
                .join(Tables.CUSTOMER)
                .on(Tables.PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.PESANAN_SATUAN.DELETED_AT.isNull())
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

            List<ItemPesananSatuanResponse> items = jooq.select(
                            Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG,
                            Tables.PRICE_ORDER_SATUAN.UKURAN,
                            Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN,
                            Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM,
                            Tables.ITEM_PESANAN_SATUAN.QTY
                    )
                    .from(Tables.ITEM_PESANAN_SATUAN)
                    .join(Tables.PRICE_ORDER_SATUAN)
                    .on(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN.eq(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN))
                    .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesanan))
                    .and(Tables.ITEM_PESANAN_SATUAN.DELETED_AT.isNull())
                    .fetch()
                    .map(record -> {
                        Double hargaDouble = record.get(Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM);
                        String hargaFormatted = hargaDouble != null ? formatter.format(hargaDouble) : "0";

                        return new ItemPesananSatuanResponse(
                                record.get(Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG),
                                record.get(Tables.PRICE_ORDER_SATUAN.UKURAN),
                                record.get(Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN),
                                hargaFormatted,
                                record.get(Tables.ITEM_PESANAN_SATUAN.QTY)
                        );
                    });

            response.setItems(items);

            responses.add(response);
        }

        return responses;
    }





    public PesananSatuanResponse createPesananSatuan(PesananSatuanRequest request) {
        List<ItemPesananSatuanRequest> itemRequests = request.getIdItemList();
        List<ItemPesananSatuanRecord> items = new ArrayList<>();

        double totalHarga = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        // Hitung total harga dan siapkan record item
        for (ItemPesananSatuanRequest itemReq : itemRequests) {
            PriceOrderSatuanResponse price = priceOrderSatuanRepository
                    .getPriceById(itemReq.getIdPriceSatuan())
                    .orElseThrow(() -> new RuntimeException("Data harga tidak ditemukan untuk ID: " + itemReq.getIdPriceSatuan()));

            double hargaSatuan = price.getHargaPerItem();
            int qty = itemReq.getQty();
            double subtotal = hargaSatuan * qty;

            ItemPesananSatuanRecord itemRecord = jooq.newRecord(Tables.ITEM_PESANAN_SATUAN);
            itemRecord.setIdItemSatuan(UuidCreator.getTimeOrderedEpoch().toString());
            itemRecord.setIdPriceSatuan(itemReq.getIdPriceSatuan());
            itemRecord.setQty(qty);

            items.add(itemRecord);
            totalHarga += subtotal;
        }

        String idPesanan = UuidCreator.getTimeOrderedEpoch().toString();
        String noFaktur = InvoiceGenerator.generateNoFaktur();

        ZoneId witaZone = ZoneId.of("Asia/Makassar");
        LocalDateTime tglMasuk = ZonedDateTime.now(witaZone).toLocalDateTime();

        PesananSatuanTipePembayaran tipePembayaranEnum = PesananSatuanTipePembayaran.valueOf(request.getTipePembayaran());
        PesananSatuanStatusBayar statusBayarEnum = PesananSatuanStatusBayar.valueOf(request.getStatusBayar());
        PesananSatuanStatusOrder statusOrderEnum = PesananSatuanStatusOrder.valueOf(request.getStatusOrder());

        // Insert ke tabel pesanan satuan
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

        // Insert item pesanan
        for (ItemPesananSatuanRecord item : items) {
            jooq.insertInto(Tables.ITEM_PESANAN_SATUAN)
                    .set(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN, item.getIdItemSatuan())
                    .set(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN, idPesanan)
                    .set(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN, item.getIdPriceSatuan())
                    .set(Tables.ITEM_PESANAN_SATUAN.QTY, item.getQty())
                    .execute();
        }

        // Update laporan harian jika perlu
        if (statusBayarEnum == PesananSatuanStatusBayar.Lunas ||
                statusBayarEnum == PesananSatuanStatusBayar.Belum_Lunas) {
            dailyIncomeService.createLaporan(tglMasuk.toLocalDate());
        }

        // Ambil data customer untuk response
        Record customerRecord = jooq.select()
                .from(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        String namaCustomer = "";
        String customerPhone = "";

        if (customerRecord != null) {
            namaCustomer = customerRecord.get(Tables.CUSTOMER.NAMA, String.class);
            customerPhone = customerRecord.get(Tables.CUSTOMER.NO_TELP, String.class);
        }

        // Ambil dan mapping items untuk response
        List<ItemPesananSatuanResponse> itemResponses = jooq.select(
                        Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG,
                        Tables.PRICE_ORDER_SATUAN.UKURAN,
                        Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN,
                        Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM,
                        Tables.ITEM_PESANAN_SATUAN.QTY
                )
                .from(Tables.ITEM_PESANAN_SATUAN)
                .join(Tables.PRICE_ORDER_SATUAN)
                .on(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN.eq(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN))
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesanan))
                .and(Tables.ITEM_PESANAN_SATUAN.DELETED_AT.isNull())
                .fetch()
                .map(record -> {
                    String kategoriBarang = record.get(Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG);
                    String ukuran = record.get(Tables.PRICE_ORDER_SATUAN.UKURAN);
                    String jenisLayanan = record.get(Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN);
                    Double hargaPerItem = record.get(Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM);
                    int qty = record.get(Tables.ITEM_PESANAN_SATUAN.QTY);

                    // Format harga jadi string berformat
                    String hargaFormatted = hargaPerItem != null ? decimalFormat.format(hargaPerItem) : "0";

                    return new ItemPesananSatuanResponse(
                            kategoriBarang,
                            ukuran,
                            jenisLayanan,
                            hargaFormatted, // pake string format
                            qty
                    );
                });


        // Buat response lengkap
        PesananSatuanResponse response = new PesananSatuanResponse();
        response.setIdPesananSatuan(idPesanan);
        response.setIdCustomer(request.getIdCustomer());
        response.setNoFaktur(noFaktur);
        response.setNamaCustomer(namaCustomer);
        response.setCustomerPhone(customerPhone);
        response.setTipePembayaran(request.getTipePembayaran());
        response.setStatusBayar(request.getStatusBayar());
        response.setStatusOrder(request.getStatusOrder());
        response.setTotalHarga(decimalFormat.format(totalHarga));
        response.setTglMasuk(tglMasuk.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        response.setTglSelesai(null);
        response.setCatatan(request.getCatatan());
        response.setItems(itemResponses);

        return response;
    }





    // Soft delete untuk order unit
    public boolean softDeleteOrderUnitById(String idPesananSatuan) {
        // Soft delete pada item anak (pesanan_satuan)
        int updatedItems = jooq.update(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesananSatuan))
                .execute();

        // Soft delete pada parent (detail_pesanan_satuan)
        int updatedParent = jooq.update(Tables.PESANAN_SATUAN)
                .set(Tables.PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesananSatuan))
                .execute();

        return updatedItems > 0 && updatedParent > 0;
    }


    // Fungsi untuk memperbarui status bayar pesanan satuan
    public OrderUnitStatusBayar updateStatusBayar(String idPesananSatuan, String statusBayar) {
        // Ambil detail pesanan satuan berdasarkan idPesananSatuan
        PesananSatuanRecord orderUnitToUpdate = jooq.selectFrom(Tables.PESANAN_SATUAN)
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesananSatuan))
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
    public OrderUnitStatusResponse updateStatusOrder(String idPesananSatuan, String statusOrder) {
        PesananSatuanRecord orderUnitTopUpdate = jooq.selectFrom(Tables.PESANAN_SATUAN)
                .where(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesananSatuan))
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