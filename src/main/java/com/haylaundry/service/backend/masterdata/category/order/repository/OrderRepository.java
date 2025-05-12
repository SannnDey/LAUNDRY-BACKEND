package com.haylaundry.service.backend.masterdata.category.order.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.*;
import com.haylaundry.service.backend.jooq.gen.tables.records.PesananRecord;
import com.haylaundry.service.backend.masterdata.category.order.models.enums.*;
import com.haylaundry.service.backend.masterdata.category.order.models.request.OrderRequest;
import com.haylaundry.service.backend.masterdata.category.order.models.response.OrderResponse;
import com.haylaundry.service.backend.utils.InvoiceGenerator;
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

    // ✅ Ambil semua data pesanan
    public List<OrderResponse> getAll() {
        return jooq.select(
                        Tables.PESANAN.ID_PESANAN,
                        Tables.CUSTOMER.ID_CUSTOMER,
                        Tables.PESANAN.NO_FAKTUR,
                        Tables.CUSTOMER.NAMA,
                        Tables.CUSTOMER.NO_TELP,
                        Tables.PESANAN.TIPE_CUCIAN,
                        Tables.PESANAN.JENIS_CUCIAN,
                        Tables.PESANAN.JENIS_BARANG,
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
                .fetch()
                .stream()
                .map(record -> new OrderResponse(
                        record.get(Tables.PESANAN.ID_PESANAN),
                        record.get(Tables.CUSTOMER.ID_CUSTOMER),
                        record.get(Tables.PESANAN.NO_FAKTUR),
                        record.get(Tables.CUSTOMER.NAMA),
                        record.get(Tables.CUSTOMER.NO_TELP),
                        String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                        String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                        record.get(Tables.PESANAN.JENIS_BARANG),
                        record.get(Tables.PESANAN.QTY),
                        record.get(Tables.PESANAN.HARGA),
                        String.valueOf(record.get(Tables.PESANAN.TIPE_PEMBAYARAN)),
                        String.valueOf(record.get(Tables.PESANAN.STATUS_BAYAR)),
                        String.valueOf(record.get(Tables.PESANAN.STATUS_ORDER)),
                        record.get(Tables.PESANAN.TGL_MASUK),
                        record.get(Tables.PESANAN.TGL_SELESAI),
                        record.get(Tables.PESANAN.CATATAN),
                        record.get(Tables.PESANAN.DELETED_AT)
                ))
                .collect(Collectors.toList());
    }

    public OrderResponse create(OrderRequest request) {
        String orderId = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();

        // ✅ 1. Pastikan customer ada
        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        // ✅ 2. Simpan data pesanan
        PesananRecord newOrder = jooq.newRecord(Tables.PESANAN);
        newOrder.setIdPesanan(orderId);
        newOrder.setIdCustomer(request.getIdCustomer());
        newOrder.setNoFaktur(InvoiceGenerator.generateNoFaktur());
        newOrder.setTipeCucian(PesananTipeCucian.lookupLiteral(request.getTipeCucian()));
        newOrder.setJenisCucian(PesananJenisCucian.lookupLiteral(request.getJenisCucian()));
        newOrder.setJenisBarang(request.getJenisBarang());
        newOrder.setQty(request.getQty());
        newOrder.setHarga(request.getHarga());
        newOrder.setTipePembayaran(PesananTipePembayaran.lookupLiteral(request.getTipePembayaran()));
        newOrder.setStatusBayar(PesananStatusBayar.lookupLiteral(request.getStatusBayar()));
        newOrder.setStatusOrder(PesananStatusOrder.lookupLiteral(request.getStatusOrder()));
        newOrder.setTglMasuk(now);
        newOrder.setTglSelesai(now);
        newOrder.setCatatan(request.getCatatan());
        newOrder.setDeletedAt(now);
        newOrder.store(); // ✅ Simpan setelah customer valid

        // ✅ 3. Ambil data dari customer (sudah tidak null)
        String namaCustomer = customer.getNama();
        String noTelpCustomer = customer.getNoTelp();

        // ✅ 4. Kembalikan response
        return new OrderResponse(
                newOrder.getIdPesanan(),
                newOrder.getIdCustomer(),
                newOrder.getNoFaktur(),
                namaCustomer,
                noTelpCustomer,
                String.valueOf(newOrder.getTipeCucian()),
                String.valueOf(newOrder.getJenisCucian()),
                newOrder.getJenisBarang(),
                newOrder.getQty(),
                newOrder.getHarga(),
                String.valueOf(newOrder.getTipePembayaran()),
                String.valueOf(newOrder.getStatusBayar()),
                String.valueOf(newOrder.getStatusOrder()),
                newOrder.getTglMasuk(),
                newOrder.getTglSelesai(),
                newOrder.getCatatan(),
                newOrder.getDeletedAt()
        );
    }


}
