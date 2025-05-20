package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.*;
import com.haylaundry.service.backend.jooq.gen.tables.records.PesananRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.order.OrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.order.OrderStatusResponse;
import com.haylaundry.service.backend.core.utils.HargaCucianKiloan;
import com.haylaundry.service.backend.core.utils.InvoiceGenerator;
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

    public OrderResponse getById(String idPesanan) {
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

        return new OrderResponse(
                record.get(Tables.PESANAN.ID_PESANAN),
                record.get(Tables.CUSTOMER.ID_CUSTOMER),
                record.get(Tables.PESANAN.NO_FAKTUR),
                record.get(Tables.CUSTOMER.NAMA),
                record.get(Tables.CUSTOMER.NO_TELP),
                String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                record.get(Tables.PESANAN.QTY),
                record.get(Tables.PESANAN.HARGA),
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

        // ✅ 1. Pastikan customer ada
        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        // ✅ 2. Menghitung harga total menggunakan HargaCucian
        PesananTipeCucian tipeCucian = PesananTipeCucian.lookupLiteral(request.getTipeCucian());
        PesananJenisCucian jenisCucian = PesananJenisCucian.lookupLiteral(request.getJenisCucian());
        double hargaTotal = HargaCucianKiloan.hitungHargaTotal(tipeCucian, jenisCucian, request.getQty()); // Menggunakan HargaCucian untuk menghitung harga

        // ✅ 3. Simpan data pesanan
        PesananRecord newOrder = jooq.newRecord(Tables.PESANAN);
        newOrder.setIdPesanan(orderId);
        newOrder.setIdCustomer(request.getIdCustomer());
        newOrder.setNoFaktur(InvoiceGenerator.generateNoFaktur());
        newOrder.setTipeCucian(tipeCucian);
        newOrder.setJenisCucian(jenisCucian);
        newOrder.setQty(request.getQty());
        newOrder.setHarga(hargaTotal); // Menetapkan harga total yang dihitung
        newOrder.setTipePembayaran(PesananTipePembayaran.lookupLiteral(request.getTipePembayaran()));
        newOrder.setStatusBayar(PesananStatusBayar.lookupLiteral(request.getStatusBayar()));
        newOrder.setStatusOrder(PesananStatusOrder.lookupLiteral(request.getStatusOrder()));
        newOrder.setTglMasuk(now);
        newOrder.setTglSelesai(null);
        newOrder.setCatatan(request.getCatatan());
        newOrder.setDeletedAt(null);
        newOrder.store(); // ✅ Simpan setelah customer valid

        // ✅ 4. Ambil data dari customer (sudah tidak null)
        String namaCustomer = customer.getNama();
        String noTelpCustomer = customer.getNoTelp();

        // ✅ 5. Kembalikan response
        return new OrderResponse(
                newOrder.getIdPesanan(),
                newOrder.getIdCustomer(),
                newOrder.getNoFaktur(),
                namaCustomer,
                noTelpCustomer,
                String.valueOf(newOrder.getTipeCucian()),
                String.valueOf(newOrder.getJenisCucian()),
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


    public OrderStatusResponse updateStatus(String idPesanan, String statusOrder) {
        // Ambil pesanan berdasarkan ID
        PesananRecord orderToUpdate = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .fetchOne();

        if (orderToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        // Update status pesanan
        PesananStatusOrder status = PesananStatusOrder.valueOf(statusOrder);
        orderToUpdate.setStatusOrder(status);

        // Jika status pesanan "Selesai", set tanggal selesai
        if (PesananStatusOrder.Selesai.equals(status)) {
            orderToUpdate.setTglSelesai(LocalDateTime.now());
        }

        // Simpan perubahan
        orderToUpdate.store();

        // Return ringkas dengan OrderStatusResponse
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
        // Ambil pesanan berdasarkan ID
        PesananRecord orderToUpdate = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .fetchOne();

        if (orderToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        // Update status bayar pesanan
        PesananStatusBayar status = PesananStatusBayar.lookupLiteral(statusBayar);
        orderToUpdate.setStatusBayar(status);

        // Simpan perubahan
        orderToUpdate.setStatusBayar(status);
        orderToUpdate.store();

        // Return ringkas dengan OrderStatusResponse
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

}
