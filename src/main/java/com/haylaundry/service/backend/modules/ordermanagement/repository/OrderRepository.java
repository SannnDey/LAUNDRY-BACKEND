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

    // ✅ Ambil semua data pesanan yang belum dihapus
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
                // Menambahkan filter untuk tidak mengambil pesanan yang sudah di-soft delete
                .where(Tables.PESANAN.DELETED_AT.isNull())  // Pastikan hanya pesanan yang belum di-soft delete
                .fetch()
                .stream()
                .map(record -> {
                    // Format harga menggunakan DecimalFormat
                    double harga = record.get(Tables.PESANAN.HARGA);
                    String formattedHarga = formatter.format(harga);  // Format harga ke string dengan pemisah ribuan

                    // Return OrderResponse dengan harga yang diformat
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

        // Format harga menggunakan DecimalFormat
        double harga = record.get(Tables.PESANAN.HARGA);
        String formattedHarga = formatter.format(harga);  // Format harga dengan pemisah ribuan

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
        // Format the nomor into the complete noFaktur string, adding "INV-" prefix
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

        // Format harga menggunakan DecimalFormat
        double harga = record.get(Tables.PESANAN.HARGA);
        String formattedHarga = formatter.format(harga);  // Format harga dengan pemisah ribuan

        return new OrderResponse(
                record.get(Tables.PESANAN.ID_PESANAN),
                record.get(Tables.CUSTOMER.ID_CUSTOMER),
                record.get(Tables.PESANAN.NO_FAKTUR),
                record.get(Tables.CUSTOMER.NAMA),
                record.get(Tables.CUSTOMER.NO_TELP),
                String.valueOf(record.get(Tables.PESANAN.TIPE_CUCIAN)),
                String.valueOf(record.get(Tables.PESANAN.JENIS_CUCIAN)),
                record.get(Tables.PESANAN.QTY),
                formattedHarga,  // Set harga yang sudah diformat dengan pemisah ribuan
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
        double hargaTotal = PriceOrder.hitungHargaTotal(tipeCucian, jenisCucian, request.getQty()); // Menggunakan HargaCucian untuk menghitung harga

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

        // Menambahkan pengecekan status bayar
        // Jika status bayar "Lunas", update laporan pemasukan
        if (PesananStatusBayar.Lunas.equals(newOrder.getStatusBayar())) {
            // Memperbarui laporan pemasukan harian berdasarkan tanggal pesanan
            dailyIncomeService.createLaporan(newOrder.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        }
        // Jika status bayar "Belum Lunas", update laporan piutang
        else if (PesananStatusBayar.Belum_Lunas.equals(newOrder.getStatusBayar())) {
            // Memperbarui laporan piutang harian jika belum lunas
            dailyIncomeService.createLaporan(newOrder.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        }

        // ✅ 4. Ambil data dari customer (sudah tidak null)
        String namaCustomer = customer.getNama();
        String noTelpCustomer = customer.getNoTelp();

        // ✅ 5. Format Harga untuk ditampilkan dengan pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedHarga = formatter.format(newOrder.getHarga());

        // ✅ 6. Kembalikan response
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



    // Fungsi untuk memperbarui status bayar pesanan
    public OrderStatusBayar updateBayarStatus(String idPesanan, String statusBayar) {
        // Ambil pesanan berdasarkan idPesanan
        PesananRecord orderToUpdate = jooq.selectFrom(Tables.PESANAN)
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .fetchOne();

        if (orderToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        // Update status bayar pesanan
        PesananStatusBayar status = PesananStatusBayar.lookupLiteral(statusBayar);
        orderToUpdate.setStatusBayar(status);

        // Simpan perubahan status bayar
        orderToUpdate.store();

        // Jika status bayar "Lunas", update laporan pemasukan
        if (PesananStatusBayar.Lunas.equals(status)) {
            // Memperbarui laporan pemasukan harian berdasarkan tanggal pesanan
            dailyIncomeService.createLaporan(orderToUpdate.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        }

        // Jika status bayar masih "Belum Lunas", kita tidak perlu melakukan apa-apa pada laporan

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
        // Melakukan soft delete dengan mengubah status kolom 'deleted' menjadi true
        int updated = jooq.update(Tables.PESANAN)
                .set(Tables.PESANAN.DELETED_AT, LocalDateTime.now())  // Mengubah status deleted menjadi true
                .where(Tables.PESANAN.ID_PESANAN.eq(idPesanan))
                .execute();

        return updated > 0;
    }

}
