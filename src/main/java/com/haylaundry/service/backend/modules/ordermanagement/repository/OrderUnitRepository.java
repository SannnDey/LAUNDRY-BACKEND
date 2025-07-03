package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.core.utils.EnumValidator;
import com.haylaundry.service.backend.core.utils.PriceOrderUnit;
import com.haylaundry.service.backend.core.utils.InvoiceGenerator;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.*;
import java.text.DecimalFormat;
import com.haylaundry.service.backend.jooq.gen.tables.records.DetailPesananSatuanRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.ItemPesananSatuanRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.OrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.DetailOrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.DetailOrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusBayar;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.OrderUnitStatusResponse;
import com.haylaundry.service.backend.modules.report.service.DailyIncomeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderUnitRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    @Inject
    private DailyIncomeService dailyIncomeService;

    // ✅ Ambil semua data pesanan unit yang belum dihapus
    public List<DetailOrderUnitResponse> getAllOrderUnit() {
        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        List<Record> records = jooq.select()
                .from(Tables.DETAIL_PESANAN_SATUAN)
                .join(Tables.ITEM_PESANAN_SATUAN)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL))
                .leftJoin(Tables.CUSTOMER)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                // Menambahkan filter untuk memeriksa DELETED_AT
                .where(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT.isNull())  // Pastikan hanya yang belum di-soft delete
                .fetch();

        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.get(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Record> groupRecords = entry.getValue();
                    Record firstRecord = groupRecords.get(0);

                    // Buat DetailOrderUnitResponse
                    DetailOrderUnitResponse detailResponse = new DetailOrderUnitResponse();
                    detailResponse.setIdDetail(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL));
                    detailResponse.setIdCustomer(firstRecord.get(Tables.CUSTOMER.ID_CUSTOMER));
                    detailResponse.setNoFaktur(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.NO_FAKTUR));
                    detailResponse.setNamaCustomer(firstRecord.get(Tables.CUSTOMER.NAMA));
                    detailResponse.setCustomerPhone(firstRecord.get(Tables.CUSTOMER.NO_TELP));
                    detailResponse.setTipePembayaran(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TIPE_PEMBAYARAN)).replace("_", " "));
                    detailResponse.setStatusBayar(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR)).replace("_", " "));
                    detailResponse.setStatusOrder(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_ORDER)).replace("_", " "));

                    // Format totalHarga menggunakan DecimalFormat
                    double totalHarga = firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA);
                    String formattedTotalHarga = formatter.format(totalHarga);  // Format totalHarga ke string

                    // Set formatted totalHarga (String)
                    detailResponse.setTotalHarga(formattedTotalHarga);

                    detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
                    detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
                    detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
                    detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

                    // Menambahkan items
                    List<OrderUnitResponse> detailItems = groupRecords.stream()
                            .map(record -> {
                                OrderUnitResponse item = new OrderUnitResponse();
                                item.setIdPesananSatuan(record.get(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN));
                                item.setIdDetail(record.get(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL));
                                item.setKategoriBarang(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.KATEGORI_BARANG)).replace("_", " "));
                                item.setUkuran(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.UKURAN)).replace("_", " "));
                                item.setJenisLayanan(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.JENIS_LAYANAN)).replace("_", " "));

                                // Format harga menggunakan DecimalFormat
                                double harga = record.get(Tables.ITEM_PESANAN_SATUAN.HARGA);
                                String formattedHarga = formatter.format(harga);  // Format harga ke string dengan pemisah ribuan

                                item.setHarga(formattedHarga);  // Set formatted harga as String
                                item.setQty(record.get(Tables.ITEM_PESANAN_SATUAN.QTY));
                                return item;
                            })
                            .collect(Collectors.toList());

                    // Set items ke dalam response
                    detailResponse.setItems(detailItems);

                    return detailResponse;
                })
                .collect(Collectors.toList());
    }



    public DetailOrderUnitResponse getOrderUnitById(String idDetail) {
        // Ambil data dari database
        List<Record> records = jooq.select()
                .from(Tables.DETAIL_PESANAN_SATUAN)
                .join(Tables.ITEM_PESANAN_SATUAN)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL))
                .leftJoin(Tables.CUSTOMER)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .fetch();

        if (records.isEmpty()) {
            throw new IllegalArgumentException("Pesanan dengan ID " + idDetail + " tidak ditemukan.");
        }

        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Ambil record pertama (karena ID_DETAIL harus unik)
        Record firstRecord = records.get(0);
        DetailOrderUnitResponse detailResponse = new DetailOrderUnitResponse();
        detailResponse.setIdDetail(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL));
        detailResponse.setIdCustomer(firstRecord.get(Tables.CUSTOMER.ID_CUSTOMER));
        detailResponse.setNoFaktur(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.NO_FAKTUR));
        detailResponse.setNamaCustomer(firstRecord.get(Tables.CUSTOMER.NAMA));
        detailResponse.setCustomerPhone(firstRecord.get(Tables.CUSTOMER.NO_TELP));
        detailResponse.setTipePembayaran(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TIPE_PEMBAYARAN)).replace("_", " "));
        detailResponse.setStatusBayar(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR)).replace("_", " "));
        detailResponse.setStatusOrder(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_ORDER)).replace("_", " "));

        // Ambil totalHarga dan format menggunakan DecimalFormat
        double totalHarga = firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA);
        String formattedTotalHarga = formatter.format(totalHarga);  // Format totalHarga menjadi string dengan pemisah ribuan

        // Set formatted totalHarga ke dalam response
        detailResponse.setTotalHarga(formattedTotalHarga);

        detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
        detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
        detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
        detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

        // Mengambil items dan format harga item
        List<OrderUnitResponse> items = records.stream().map(record -> {
            OrderUnitResponse item = new OrderUnitResponse();
            item.setIdPesananSatuan(record.get(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN));
            item.setIdDetail(record.get(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL));
            item.setKategoriBarang(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.KATEGORI_BARANG)).replace("_", " "));
            item.setUkuran(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.UKURAN)).replace("_", " "));
            item.setJenisLayanan(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.JENIS_LAYANAN)).replace("_", " "));

            // Format harga menggunakan DecimalFormat
            double harga = record.get(Tables.ITEM_PESANAN_SATUAN.HARGA);
            String formattedHarga = formatter.format(harga);  // Format harga ke string dengan pemisah ribuan

            item.setHarga(formattedHarga);  // Set formatted harga as String
            item.setQty(record.get(Tables.ITEM_PESANAN_SATUAN.QTY));
            return item;
        }).collect(Collectors.toList());

        // Set items ke dalam response
        detailResponse.setItems(items);

        return detailResponse;
    }


    public DetailOrderUnitResponse getOrderUnitByNoFaktur(String nomor) {
        // Format the nomor into the complete noFaktur string, adding "INV-" prefix
        String noFaktur = "INV-" + nomor;

        List<Record> records = jooq.select()
                .from(Tables.DETAIL_PESANAN_SATUAN)
                .join(Tables.ITEM_PESANAN_SATUAN)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL))
                .leftJoin(Tables.CUSTOMER)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.DETAIL_PESANAN_SATUAN.NO_FAKTUR.eq(noFaktur))
                .fetch();

        if (records.isEmpty()) {
            throw new IllegalArgumentException("Pesanan dengan No Faktur " + noFaktur + " tidak ditemukan.");
        }

        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Ambil record pertama (karena ID_DETAIL harus unik)
        Record firstRecord = records.get(0);
        DetailOrderUnitResponse detailResponse = new DetailOrderUnitResponse();
        detailResponse.setIdDetail(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL));
        detailResponse.setIdCustomer(firstRecord.get(Tables.CUSTOMER.ID_CUSTOMER));
        detailResponse.setNoFaktur(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.NO_FAKTUR));
        detailResponse.setNamaCustomer(firstRecord.get(Tables.CUSTOMER.NAMA));
        detailResponse.setCustomerPhone(firstRecord.get(Tables.CUSTOMER.NO_TELP));
        detailResponse.setTipePembayaran(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TIPE_PEMBAYARAN)).replace("_", " "));
        detailResponse.setStatusBayar(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR)).replace("_", " "));
        detailResponse.setStatusOrder(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_ORDER)).replace("_", " "));

        // Ambil totalHarga dan format menggunakan DecimalFormat
        double totalHarga = firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA);
        String formattedTotalHarga = formatter.format(totalHarga);  // Format totalHarga menjadi string dengan pemisah ribuan

        // Set formatted totalHarga ke dalam response
        detailResponse.setTotalHarga(formattedTotalHarga);

        detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
        detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
        detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
        detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

        // Mengambil items dan format harga item
        List<OrderUnitResponse> items = records.stream().map(record -> {
            OrderUnitResponse item = new OrderUnitResponse();
            item.setIdPesananSatuan(record.get(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN));
            item.setIdDetail(record.get(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL));
            item.setKategoriBarang(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.KATEGORI_BARANG)).replace("_", " "));
            item.setUkuran(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.UKURAN)).replace("_", " "));
            item.setJenisLayanan(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.JENIS_LAYANAN)).replace("_", " "));

            // Format harga menggunakan DecimalFormat
            double harga = record.get(Tables.ITEM_PESANAN_SATUAN.HARGA);
            String formattedHarga = formatter.format(harga);  // Format harga ke string dengan pemisah ribuan

            item.setHarga(formattedHarga);  // Set formatted harga as String
            item.setQty(record.get(Tables.ITEM_PESANAN_SATUAN.QTY));
            return item;
        }).collect(Collectors.toList());

        // Set items ke dalam response
        detailResponse.setItems(items);

        return detailResponse;
    }




    public DetailOrderUnitResponse createOrderUnit(DetailOrderUnitRequest request) {
        ZoneId witaZone = ZoneId.of("Asia/Makassar");
        LocalDateTime now = ZonedDateTime.now(witaZone).toLocalDateTime();

        // Validasi customer ada
        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        // Generate ID dan nomor faktur untuk detail pesanan
        String detailId = UuidCreator.getTimeOrderedEpoch().toString();
        String noFaktur = InvoiceGenerator.generateNoFaktur();

        // Validasi enum tipe pembayaran, status bayar, dan status order di detail
        var tipePembayaran = EnumValidator.validateEnum(
                DetailPesananSatuanTipePembayaran.class,
                request.getTipePembayaran(),
                "Tipe Pembayaran"
        );
        var statusBayar = EnumValidator.validateEnum(
                DetailPesananSatuanStatusBayar.class,
                request.getStatusBayar(),
                "Status Bayar"
        );
        var statusOrder = EnumValidator.validateEnum(
                DetailPesananSatuanStatusOrder.class,
                request.getStatusOrder(),
                "Status Order"
        );

        // Simpan dulu DetailPesananSatuan ke DB (parent) dengan totalHarga 0 sementara
        DetailPesananSatuanRecord detailRecord = jooq.newRecord(Tables.DETAIL_PESANAN_SATUAN);
        detailRecord.setIdDetail(detailId);
        detailRecord.setIdCustomer(request.getIdCustomer());
        detailRecord.setNoFaktur(noFaktur);
        detailRecord.setTipePembayaran(tipePembayaran);
        detailRecord.setStatusBayar(statusBayar);
        detailRecord.setStatusOrder(statusOrder);
        detailRecord.setTotalHarga(0.0); // sementara 0, akan diupdate nanti
        detailRecord.setTglMasuk(now);
        detailRecord.setTglSelesai(request.getTglSelesai());
        detailRecord.setCatatan(request.getCatatan());
        detailRecord.setDeletedAt(request.getDeletedAt());
        detailRecord.store();


        // List untuk menampung response OrderUnitResponse
        List<OrderUnitResponse> orderUnitResponses = new ArrayList<>();

        // Variabel untuk menghitung total harga semua item
        double totalHarga = 0.0;

        // Format untuk pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Iterasi setiap item OrderUnitRequest dalam DetailOrderUnitRequest.items
        for (OrderUnitRequest item : request.getItems()) {
            // Validasi qty minimal 1
            if (item.getQty() < 1) {
                throw new IllegalArgumentException("Quantity untuk item kategori " + item.getKategoriBarang() + " minimal 1.");
            }

            // Validasi enum kategori, ukuran, dan jenis layanan di setiap item
            var kategori = EnumValidator.validateEnum(
                    ItemPesananSatuanKategoriBarang.class,
                    item.getKategoriBarang(),
                    "Kategori Barang"
            );
            var ukuran = EnumValidator.validateEnum(
                    ItemPesananSatuanUkuran.class,
                    item.getUkuran(),
                    "Ukuran"
            );
            var jenisLayanan = EnumValidator.validateEnum(
                    ItemPesananSatuanJenisLayanan.class,
                    item.getJenisLayanan(),
                    "Jenis Layanan"
            );

            // Hitung harga otomatis
            double hargaHitung = PriceOrderUnit.hitungHarga(kategori, ukuran, jenisLayanan);
            double hargaFinal = hargaHitung * item.getQty();

            // Tambahkan ke total harga
            totalHarga += hargaFinal;

            // Format hargaFinal menjadi string dengan pemisah ribuan
            String formattedHargaFinal = formatter.format(hargaFinal);

            // Simpan PesananSatuan record untuk tiap item (child)
            ItemPesananSatuanRecord orderUnitRecord = jooq.newRecord(Tables.ITEM_PESANAN_SATUAN);
            orderUnitRecord.setIdItemSatuan(UuidCreator.getTimeOrderedEpoch().toString());
            orderUnitRecord.setIdDetail(detailId); // FK ke detail pesanan yang sudah ada
            orderUnitRecord.setKategoriBarang(kategori);
            orderUnitRecord.setUkuran(ukuran);
            orderUnitRecord.setJenisLayanan(jenisLayanan);
            orderUnitRecord.setHarga(hargaFinal);
            orderUnitRecord.setQty(item.getQty());
            orderUnitRecord.store();



            // Tambahkan ke response list
            orderUnitResponses.add(new OrderUnitResponse(
                    orderUnitRecord.getIdItemSatuan(),
                    detailId,
                    kategori.getLiteral(),
                    ukuran.getLiteral(),
                    jenisLayanan.getLiteral(),
                    formattedHargaFinal,
                    orderUnitRecord.getQty()
            ));
        }

        // Menggunakan DecimalFormat untuk memformat totalHarga dengan pemisah ribuan
        String formattedTotalHarga = formatter.format(totalHarga);

        // Update totalHarga di detailRecord setelah insert semua pesanan_satuan
        detailRecord.setTotalHarga(totalHarga);  // Simpan totalHarga dalam bentuk double
        detailRecord.store();

        // Update laporan otomatis setelah menyimpan pesanan satuan
        if (DetailPesananSatuanStatusBayar.Lunas.equals(detailRecord.getStatusBayar())) {
            dailyIncomeService.createLaporan(detailRecord.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        } else if (DetailPesananSatuanStatusBayar.Belum_Lunas.equals(detailRecord.getStatusBayar())) {
            // Jika status bayar belum lunas, kita tidak update laporan pemasukan, tapi tetap ke piutang
            dailyIncomeService.createLaporan(detailRecord.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        }

        // Build dan return response DetailOrderUnitResponse dengan list items
        return new DetailOrderUnitResponse(
                detailId,
                request.getIdCustomer(),
                noFaktur,
                customer.getNama(),
                customer.getNoTelp(),
                request.getTipePembayaran(),
                request.getStatusBayar(),
                request.getStatusOrder(),
                formattedTotalHarga,
                detailRecord.getTglMasuk(),
                detailRecord.getTglSelesai(),
                detailRecord.getCatatan(),
                detailRecord.getDeletedAt(),
                orderUnitResponses
        );
    }



    // Fungsi untuk memperbarui status bayar pesanan satuan
    public OrderUnitStatusBayar updateStatusBayar(String idDetail, String statusBayar) {
        // Ambil detail pesanan satuan berdasarkan idDetail
        DetailPesananSatuanRecord orderUnitToUpdate = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .fetchOne();

        if (orderUnitToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        // Gunakan lookupLiteral untuk mencari enum dari string literal
        DetailPesananSatuanStatusBayar status = DetailPesananSatuanStatusBayar.lookupLiteral(statusBayar);

        if (status == null) {
            throw new IllegalArgumentException("Status bayar tidak valid: " + statusBayar);
        }

        // Update status bayar
        orderUnitToUpdate.setStatusBayar(status);
        orderUnitToUpdate.store();

        // Jika status bayar "Lunas", update laporan pemasukan
        if (DetailPesananSatuanStatusBayar.Lunas.equals(status)) {
            // Memperbarui laporan pemasukan harian berdasarkan tanggal pesanan satuan
            dailyIncomeService.createLaporan(orderUnitToUpdate.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        } else if (DetailPesananSatuanStatusBayar.Belum_Lunas.equals(status)) {
            // Memperbarui laporan piutang harian jika belum lunas
            dailyIncomeService.createLaporan(orderUnitToUpdate.getTglMasuk().toLocalDate());  // Pastikan menggunakan LocalDate
        }

        return new OrderUnitStatusBayar(
                orderUnitToUpdate.getIdDetail(),
                orderUnitToUpdate.getNoFaktur(),
                status.getLiteral() // Pastikan ini yang dikirim ke FE
        );
    }

    // Fungsi untuk memperbarui status order pesanan satuan
    public OrderUnitStatusResponse updateStatusOrder(String idDetail, String statusOrder) {
        DetailPesananSatuanRecord orderUnitTopUpdate = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .fetchOne();

        if (orderUnitTopUpdate == null ) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        DetailPesananSatuanStatusOrder status = DetailPesananSatuanStatusOrder.lookupLiteral(statusOrder);
        orderUnitTopUpdate.setStatusOrder(status);

        if (DetailPesananSatuanStatusOrder.Selesai.equals(status)) {
            orderUnitTopUpdate.setTglSelesai(LocalDateTime.now());
        }

        orderUnitTopUpdate.setStatusOrder(status);
        orderUnitTopUpdate.store();

        return new OrderUnitStatusResponse(
                orderUnitTopUpdate.getIdDetail(),
                orderUnitTopUpdate.getNoFaktur(),
                orderUnitTopUpdate.getStatusBayar().toString(),
                status.getLiteral(),
                orderUnitTopUpdate.getTglSelesai()
        );
    }



    public boolean deleteOrderUnitById(String idDetail) {
        // Hapus dulu item anak (pesanan_satuan)
        int deletedItems = jooq.deleteFrom(Tables.ITEM_PESANAN_SATUAN)
                .where(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        // Hapus parent (detail_pesanan_satuan)
        int deletedParent = jooq.deleteFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        return deletedParent > 0;
    }


    // Soft delete untuk order unit
    public boolean softDeleteOrderUnitById(String idDetail) {
        // Soft delete pada item anak (pesanan_satuan)
        int updatedItems = jooq.update(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        // Soft delete pada parent (detail_pesanan_satuan)
        int updatedParent = jooq.update(Tables.DETAIL_PESANAN_SATUAN)
                .set(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        return updatedItems > 0 && updatedParent > 0;
    }


}