package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderRepository extends JooqRepository {
    @Inject
    private DSLContext jooq;

    @Inject
    private DailyIncomeService dailyIncomeService;

    public List<OrderResponse> getAll() {
        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

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
                    // Format harga menggunakan DecimalFormat
                    double harga = record.get(Tables.PESANAN.HARGA);
                    String formattedHarga = formatter.format(harga);

                    return new OrderResponse(
                            record.get(Tables.PESANAN.ID_PESANAN),
                            record.get(Tables.CUSTOMER.ID_CUSTOMER),
                            record.get(Tables.PESANAN.NO_FAKTUR),
                            record.get(Tables.CUSTOMER.NAMA),
                            record.get(Tables.CUSTOMER.NO_TELP),
                            String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                            String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                            record.get(Tables.PESANAN.QTY),
                            formattedHarga,  // Set harga yang sudah diformat
                            String.valueOf(record.get(Tables.PESANAN.TIPE_PEMBAYARAN)),
                            String.valueOf(record.get(Tables.PESANAN.STATUS_BAYAR)),
                            String.valueOf(record.get(Tables.PESANAN.STATUS_ORDER)),
                            record.get(Tables.PESANAN.TGL_MASUK),
                            record.get(Tables.PESANAN.TGL_SELESAI),
                            record.get(Tables.PESANAN.CATATAN),
                            record.get(Tables.PESANAN.DELETED_AT)
                    );
                })
                .collect(Collectors.toList());
    }


    public OrderResponse getById(String idPesanan) {
        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

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

        double harga = record.get(Tables.PESANAN.HARGA);
        String formattedHarga = formatter.format(harga);

        return new OrderResponse(
                record.get(Tables.PESANAN.ID_PESANAN),
                record.get(Tables.CUSTOMER.ID_CUSTOMER),
                record.get(Tables.PESANAN.NO_FAKTUR),
                record.get(Tables.CUSTOMER.NAMA),
                record.get(Tables.CUSTOMER.NO_TELP),
                String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                record.get(Tables.PESANAN.QTY),
                formattedHarga,  // Set harga yang diformat dengan pemisah ribuan
                String.valueOf(record.get(Tables.PESANAN.TIPE_PEMBAYARAN)),
                String.valueOf(record.get(Tables.PESANAN.STATUS_BAYAR)),
                String.valueOf(record.get(Tables.PESANAN.STATUS_ORDER)),
                record.get(Tables.PESANAN.TGL_MASUK),
                record.get(Tables.PESANAN.TGL_SELESAI),
                record.get(Tables.PESANAN.CATATAN),
                record.get(Tables.PESANAN.DELETED_AT)
        );
    }


    public OrderResponse getByNoFaktur(String nomor) {
        String noFaktur = "INV-" + nomor;

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

        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        double harga = record.get(Tables.PESANAN.HARGA);
        String formattedHarga = formatter.format(harga);

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
                record.get(Tables.PESANAN.TGL_MASUK),
                record.get(Tables.PESANAN.TGL_SELESAI),
                record.get(Tables.PESANAN.CATATAN),
                record.get(Tables.PESANAN.DELETED_AT)
        );
    }




    public OrderResponse create(OrderRequest request) {
        String orderId = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();

        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        PesananTipeCucian tipeCucian = PesananTipeCucian.lookupLiteral(request.getTipeCucian());
        PesananJenisCucian jenisCucian = PesananJenisCucian.lookupLiteral(request.getJenisCucian());
        double hargaTotal = PriceOrder.hitungHargaTotal(tipeCucian, jenisCucian, request.getQty());

        PesananRecord newOrder = jooq.newRecord(Tables.PESANAN);
        newOrder.setIdPesanan(orderId);
        newOrder.setIdCustomer(request.getIdCustomer());
        newOrder.setNoFaktur(InvoiceGenerator.generateNoFaktur());
        newOrder.setTipeCucian(tipeCucian);
        newOrder.setJenisCucian(jenisCucian);
        newOrder.setQty(request.getQty());
        newOrder.setHarga(hargaTotal);
        newOrder.setTipePembayaran(PesananTipePembayaran.lookupLiteral(request.getTipePembayaran()));
        newOrder.setStatusBayar(PesananStatusBayar.lookupLiteral(request.getStatusBayar()));
        newOrder.setStatusOrder(PesananStatusOrder.lookupLiteral(request.getStatusOrder()));
        newOrder.setTglMasuk(now);
        newOrder.setTglSelesai(null);
        newOrder.setCatatan(request.getCatatan());
        newOrder.setDeletedAt(null);
        newOrder.store();

        if (PesananStatusBayar.Lunas.equals(newOrder.getStatusBayar())) {
            dailyIncomeService.createLaporan(newOrder.getTglMasuk().toLocalDate());
        }
        else if (PesananStatusBayar.Belum_Lunas.equals(newOrder.getStatusBayar())) {
            dailyIncomeService.createLaporan(newOrder.getTglMasuk().toLocalDate());
        }

        String namaCustomer = customer.getNama();
        String noTelpCustomer = customer.getNoTelp();

        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedHarga = formatter.format(newOrder.getHarga());

        return new OrderResponse(
                newOrder.getIdPesanan(),
                newOrder.getIdCustomer(),
                newOrder.getNoFaktur(),
                namaCustomer,
                noTelpCustomer,
                String.valueOf(newOrder.getTipeCucian()),
                String.valueOf(newOrder.getJenisCucian()),
                newOrder.getQty(),
                formattedHarga,  // Menampilkan harga yang diformat
                String.valueOf(newOrder.getTipePembayaran()),
                String.valueOf(newOrder.getStatusBayar()),
                String.valueOf(newOrder.getStatusOrder()),
                newOrder.getTglMasuk(),
                newOrder.getTglSelesai(),
                newOrder.getCatatan(),
                newOrder.getDeletedAt()
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
