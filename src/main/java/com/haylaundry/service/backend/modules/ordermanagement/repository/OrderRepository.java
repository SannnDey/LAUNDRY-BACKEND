package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.core.utils.EnumMapper;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.*;
import java.text.DecimalFormat;
import com.haylaundry.service.backend.jooq.gen.tables.records.PesananRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.order.OrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusResponse;
import com.haylaundry.service.backend.core.utils.PriceOrder;
import com.haylaundry.service.backend.core.utils.InvoiceGenerator;
import com.haylaundry.service.backend.modules.report.service.DailyIncomeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderRepository extends JooqRepository {
    @Inject
    private DSLContext jooq;
    @Inject
    private PriceOrder priceOrder;

    @Inject
    private DailyIncomeService dailyIncomeService;

    public List<OrderResponse> getAll() {
        // Format harga pakai titik
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        // Format tanggal
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return jooq.select(
                        Tables.PESANAN.ID_PESANAN,
                        Tables.CUSTOMER.ID_CUSTOMER,
                        Tables.PESANAN.NO_FAKTUR,
                        Tables.CUSTOMER.NAMA,
                        Tables.CUSTOMER.NO_TELP,
                        Tables.PESANAN.TIPE_CUCIAN,
                        Tables.PESANAN.JENIS_CUCIAN,
                        Tables.PESANAN.QTY,
                        Tables.PESANAN.HARGA,
                        Tables.PESANAN.TIPE_PEMBAYARAN,
                        Tables.PESANAN.STATUS_BAYAR,
                        Tables.PESANAN.STATUS_ORDER,
                        Tables.PESANAN.TGL_MASUK,
                        Tables.PESANAN.TGL_SELESAI,
                        Tables.PESANAN.CATATAN,
                        Tables.PESANAN.DELETED_AT
                )
                .from(Tables.PESANAN)
                .leftJoin(Tables.CUSTOMER).on(Tables.PESANAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.PESANAN.DELETED_AT.isNull())
                .fetch()
                .stream()
                .map(record -> {
                    // Format harga
                    double harga = record.get(Tables.PESANAN.HARGA);
                    String formattedHarga = formatter.format(harga);

                    // Format tanggal masuk
                    LocalDateTime tglMasuk = record.get(Tables.PESANAN.TGL_MASUK);
                    String formattedTglMasuk = tglMasuk != null ? tglMasuk.format(dateFormatter) : null;

                    // Format tanggal selesai
                    LocalDateTime tglSelesai = record.get(Tables.PESANAN.TGL_SELESAI);
                    String formattedTglSelesai = tglSelesai != null ? tglSelesai.format(dateFormatter) : null;

                    return new OrderResponse(
                            record.get(Tables.PESANAN.ID_PESANAN),
                            record.get(Tables.CUSTOMER.ID_CUSTOMER),
                            record.get(Tables.PESANAN.NO_FAKTUR),
                            record.get(Tables.CUSTOMER.NAMA),
                            record.get(Tables.CUSTOMER.NO_TELP),
                            String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                            String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                            record.get(Tables.PESANAN.QTY),
                            formattedHarga,  // Harga sudah diformat
                            String.valueOf(record.get(Tables.PESANAN.TIPE_PEMBAYARAN)),
                            String.valueOf(record.get(Tables.PESANAN.STATUS_BAYAR)),
                            String.valueOf(record.get(Tables.PESANAN.STATUS_ORDER)),
                            formattedTglMasuk,
                            formattedTglSelesai,
                            record.get(Tables.PESANAN.CATATAN),
                            record.get(Tables.PESANAN.DELETED_AT)
                    );
                })
                .collect(Collectors.toList());
    }



    public OrderResponse getById(String idPesanan) {
        // Format harga pakai titik untuk ribuan, koma untuk desimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        // Format tanggal
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        var record = jooq.select(
                        Tables.PESANAN.ID_PESANAN,
                        Tables.CUSTOMER.ID_CUSTOMER,
                        Tables.PESANAN.NO_FAKTUR,
                        Tables.CUSTOMER.NAMA,
                        Tables.CUSTOMER.NO_TELP,
                        Tables.PESANAN.TIPE_CUCIAN,
                        Tables.PESANAN.JENIS_CUCIAN,
                        Tables.PESANAN.QTY,
                        Tables.PESANAN.HARGA,
                        Tables.PESANAN.TIPE_PEMBAYARAN,
                        Tables.PESANAN.STATUS_BAYAR,
                        Tables.PESANAN.STATUS_ORDER,
                        Tables.PESANAN.TGL_MASUK,
                        Tables.PESANAN.TGL_SELESAI,
                        Tables.PESANAN.CATATAN,
                        Tables.PESANAN.DELETED_AT
                )
                .from(Tables.PESANAN)
                .leftJoin(Tables.CUSTOMER).on(Tables.PESANAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .fetchOne();

        if (record == null) {
            throw new IllegalArgumentException("Pesanan dengan ID " + idPesanan + " tidak ditemukan.");
        }

        // Format harga
        double harga = record.get(Tables.PESANAN.HARGA);
        String formattedHarga = formatter.format(harga);

        // Format tanggal masuk
        LocalDateTime tglMasuk = record.get(Tables.PESANAN.TGL_MASUK);
        String formattedTglMasuk = tglMasuk != null ? tglMasuk.format(dateFormatter) : null;

        // Format tanggal selesai
        LocalDateTime tglSelesai = record.get(Tables.PESANAN.TGL_SELESAI);
        String formattedTglSelesai = tglSelesai != null ? tglSelesai.format(dateFormatter) : null;

        return new OrderResponse(
                record.get(Tables.PESANAN.ID_PESANAN),
                record.get(Tables.CUSTOMER.ID_CUSTOMER),
                record.get(Tables.PESANAN.NO_FAKTUR),
                record.get(Tables.CUSTOMER.NAMA),
                record.get(Tables.CUSTOMER.NO_TELP),
                String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                record.get(Tables.PESANAN.QTY),
                formattedHarga, // harga sudah diformat
                String.valueOf(record.get(Tables.PESANAN.TIPE_PEMBAYARAN)),
                String.valueOf(record.get(Tables.PESANAN.STATUS_BAYAR)),
                String.valueOf(record.get(Tables.PESANAN.STATUS_ORDER)),
                formattedTglMasuk,
                formattedTglSelesai,
                record.get(Tables.PESANAN.CATATAN),
                record.get(Tables.PESANAN.DELETED_AT)
        );
    }



    public OrderResponse getByNoFaktur(String nomor) {
        String noFaktur = "INV-" + nomor;

        // Setup format harga
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);

        // Setup format tanggal
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        var record = jooq.select(
                        Tables.PESANAN.ID_PESANAN,
                        Tables.CUSTOMER.ID_CUSTOMER,
                        Tables.PESANAN.NO_FAKTUR,
                        Tables.CUSTOMER.NAMA,
                        Tables.CUSTOMER.NO_TELP,
                        Tables.PESANAN.TIPE_CUCIAN,
                        Tables.PESANAN.JENIS_CUCIAN,
                        Tables.PESANAN.QTY,
                        Tables.PESANAN.HARGA,
                        Tables.PESANAN.TIPE_PEMBAYARAN,
                        Tables.PESANAN.STATUS_BAYAR,
                        Tables.PESANAN.STATUS_ORDER,
                        Tables.PESANAN.TGL_MASUK,
                        Tables.PESANAN.TGL_SELESAI,
                        Tables.PESANAN.CATATAN,
                        Tables.PESANAN.DELETED_AT
                )
                .from(Tables.PESANAN)
                .leftJoin(Tables.CUSTOMER).on(Tables.PESANAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.PESANAN.NO_FAKTUR.eq(noFaktur))
                .fetchOne();

        if (record == null) {
            throw new IllegalArgumentException("Pesanan dengan No Faktur " + noFaktur + " tidak ditemukan.");
        }

        // Format harga
        double harga = record.get(Tables.PESANAN.HARGA);
        String formattedHarga = formatter.format(harga);

        // Format tanggal masuk (WITA)
        LocalDateTime tglMasuk = convertToWITA(record.get(Tables.PESANAN.TGL_MASUK));
        String formattedTglMasuk = tglMasuk != null ? tglMasuk.format(dateFormatter) : null;

        // Format tanggal selesai (WITA)
        LocalDateTime tglSelesai = convertToWITA(record.get(Tables.PESANAN.TGL_SELESAI));
        String formattedTglSelesai = tglSelesai != null ? tglSelesai.format(dateFormatter) : null;

        return new OrderResponse(
                record.get(Tables.PESANAN.ID_PESANAN),
                record.get(Tables.CUSTOMER.ID_CUSTOMER),
                record.get(Tables.PESANAN.NO_FAKTUR),
                record.get(Tables.CUSTOMER.NAMA),
                record.get(Tables.CUSTOMER.NO_TELP),
                String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                record.get(Tables.PESANAN.QTY),
                formattedHarga,
                String.valueOf(record.get(Tables.PESANAN.TIPE_PEMBAYARAN)),
                String.valueOf(record.get(Tables.PESANAN.STATUS_BAYAR)),
                String.valueOf(record.get(Tables.PESANAN.STATUS_ORDER)),
                formattedTglMasuk,
                formattedTglSelesai,
                record.get(Tables.PESANAN.CATATAN),
                record.get(Tables.PESANAN.DELETED_AT)
        );
    }



    private LocalDateTime convertToWITA(LocalDateTime localDateTimeFromDb) {
        if (localDateTimeFromDb == null) return null;
        // Anggap waktu di DB disimpan dalam timezone server (default JVM)
        ZonedDateTime serverZoned = localDateTimeFromDb.atZone(ZoneId.systemDefault());
        ZonedDateTime witaZoned = serverZoned.withZoneSameInstant(ZoneId.of("Asia/Makassar"));
        return witaZoned.toLocalDateTime();
    }


    public OrderResponse create(OrderRequest request) {
        String orderId = UuidCreator.getTimeOrderedEpoch().toString();
        ZoneId witaZone = ZoneId.of("Asia/Makassar");
        LocalDateTime now = ZonedDateTime.now(witaZone).toLocalDateTime();

        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        PesananTipeCucian tipeEnum = EnumMapper.toTipeCucianEnum(request.getTipeCucian());
        PesananJenisCucian jenisEnum = EnumMapper.toJenisCucianEnum(request.getJenisCucian());

        PriceOrderTipeCucian priceTipeEnum = EnumMapper.toPriceOrderTipeCucian(tipeEnum);
        PriceOrderJenisCucian priceJenisEnum = EnumMapper.toPriceOrderJenisCucian(jenisEnum);

        double totalHarga = priceOrder.hitungHargaTotal(priceTipeEnum, priceJenisEnum, request.getQty());

        PesananRecord newOrder = jooq.newRecord(Tables.PESANAN);
        newOrder.setIdPesanan(orderId);
        newOrder.setIdCustomer(request.getIdCustomer());
        newOrder.setNoFaktur(InvoiceGenerator.generateNoFaktur());
        newOrder.setTipeCucian(tipeEnum);
        newOrder.setJenisCucian(jenisEnum);
        newOrder.setQty(request.getQty());
        newOrder.setHarga(totalHarga);
        newOrder.setTipePembayaran(PesananTipePembayaran.lookupLiteral(request.getTipePembayaran()));
        newOrder.setStatusBayar(PesananStatusBayar.lookupLiteral(request.getStatusBayar()));
        newOrder.setStatusOrder(PesananStatusOrder.lookupLiteral(request.getStatusOrder()));
        newOrder.setTglMasuk(now);
        newOrder.setTglSelesai(null);
        newOrder.setCatatan(request.getCatatan());
        newOrder.setDeletedAt(null);
        newOrder.store();

        if (PesananStatusBayar.Lunas.equals(newOrder.getStatusBayar()) ||
                PesananStatusBayar.Belum_Lunas.equals(newOrder.getStatusBayar())) {
            dailyIncomeService.createLaporan(newOrder.getTglMasuk().toLocalDate());
        }

        String namaCustomer = customer.getNama();
        String noTelpCustomer = customer.getNoTelp();

        // Format harga dengan titik sebagai ribuan, koma sebagai desimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        String formattedHarga = formatter.format(newOrder.getHarga());

        // Format tanggal ke dd-MM-yyyy HH:mm
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        String formattedTglMasuk = convertToWITA(newOrder.getTglMasuk()) != null
                ? convertToWITA(newOrder.getTglMasuk()).format(dateFormatter)
                : null;

        String formattedTglSelesai = convertToWITA(newOrder.getTglSelesai()) != null
                ? convertToWITA(newOrder.getTglSelesai()).format(dateFormatter)
                : null;


        return new OrderResponse(
                newOrder.getIdPesanan(),
                newOrder.getIdCustomer(),
                newOrder.getNoFaktur(),
                namaCustomer,
                noTelpCustomer,
                String.valueOf(newOrder.getTipeCucian()),
                String.valueOf(newOrder.getJenisCucian()),
                newOrder.getQty(),
                formattedHarga,
                String.valueOf(newOrder.getTipePembayaran()),
                String.valueOf(newOrder.getStatusBayar()),
                String.valueOf(newOrder.getStatusOrder()),
                formattedTglMasuk,
                formattedTglSelesai,
                newOrder.getCatatan(),
                convertToWITA(newOrder.getDeletedAt())
        );
    }



    public OrderStatusResponse updateStatus(String idPesanan, String statusOrder) {
        PesananRecord orderToUpdate = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .fetchOne();

        if (orderToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        PesananStatusOrder status = PesananStatusOrder.valueOf(statusOrder);
        orderToUpdate.setStatusOrder(status);

        if (PesananStatusOrder.Selesai.equals(status)) {
            orderToUpdate.setTglSelesai(LocalDateTime.now());
        }

        orderToUpdate.store();

        return new OrderStatusResponse(
                orderToUpdate.getIdPesanan(),
                orderToUpdate.getNoFaktur(),
                orderToUpdate.getTipeCucian(),
                orderToUpdate.getJenisCucian(),
                orderToUpdate.getStatusBayar(),
                orderToUpdate.getStatusOrder().toString(),
                orderToUpdate.getTglSelesai()
        );
    }


    public OrderStatusBayar updateBayarStatus(String idPesanan, String statusBayar) {
        // Ambil pesanan berdasarkan idPesanan
        PesananRecord orderToUpdate = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .fetchOne();

        if (orderToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        PesananStatusBayar status = PesananStatusBayar.lookupLiteral(statusBayar);
        orderToUpdate.setStatusBayar(status);

        orderToUpdate.store();

        if (PesananStatusBayar.Lunas.equals(status)) {
            dailyIncomeService.createLaporan(orderToUpdate.getTglMasuk().toLocalDate());
        }

        return new OrderStatusBayar(
                orderToUpdate.getIdPesanan(),
                orderToUpdate.getNoFaktur(),
                status.getLiteral()
        );
    }



    public boolean deleteById(String idPesanan) {
        int deleted = jooq.deleteFrom(Tables.PESANAN)
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .execute();

        return deleted > 0;
    }


    public boolean softDeleteById(String idPesanan) {
        int updated = jooq.update(Tables.PESANAN)
                .set(Tables.PESANAN.DELETED_AT, LocalDateTime.now())
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .execute();

        return updated > 0;
    }

}
