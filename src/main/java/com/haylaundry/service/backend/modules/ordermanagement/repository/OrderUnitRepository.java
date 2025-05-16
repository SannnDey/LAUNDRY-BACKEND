package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.core.utils.EnumValidator;
import com.haylaundry.service.backend.core.utils.HargaCucianSatuan;
import com.haylaundry.service.backend.core.utils.InvoiceGenerator;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.*;
import com.haylaundry.service.backend.jooq.gen.tables.records.DetailPesananSatuanRecord;
import com.haylaundry.service.backend.jooq.gen.tables.records.PesananSatuanRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.OrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.DetailOrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.DetailOrderUnitResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderUnitRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    public List<DetailOrderUnitResponse> getAllOrderUnit() {
        List<Record> records = jooq.select()
                .from(Tables.DETAIL_PESANAN_SATUAN)
                .join(Tables.PESANAN_SATUAN)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(Tables.PESANAN_SATUAN.ID_DETAIL))
                .leftJoin(Tables.CUSTOMER)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
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

                    DetailOrderUnitResponse detailResponse = new DetailOrderUnitResponse();
                    detailResponse.setIdDetail(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL));
                    detailResponse.setIdCustomer(firstRecord.get(Tables.CUSTOMER.ID_CUSTOMER));
                    detailResponse.setNoFaktur(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.NO_FAKTUR));
                    detailResponse.setNamaCustomer(firstRecord.get(Tables.CUSTOMER.NAMA));
                    detailResponse.setCustomerPhone(firstRecord.get(Tables.CUSTOMER.NO_TELP));
                    detailResponse.setTipePembayaran(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TIPE_PEMBAYARAN)));
                    detailResponse.setStatusBayar(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR)));
                    detailResponse.setStatusOrder(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_ORDER)));
                    detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
                    detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
                    detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
                    detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

                    List<OrderUnitResponse> detailItems = groupRecords.stream()
                            .map(record -> {
                                OrderUnitResponse item = new OrderUnitResponse();
                                item.setIdPesananSatuan(record.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN));
                                item.setIdDetail(record.get(Tables.PESANAN_SATUAN.ID_DETAIL));
                                item.setKategoriBarang(String.valueOf(record.get(Tables.PESANAN_SATUAN.KATEGORI_BARANG)));
                                item.setUkuran(String.valueOf(record.get(Tables.PESANAN_SATUAN.UKURAN)));
                                item.setJenisLayanan(String.valueOf(record.get(Tables.PESANAN_SATUAN.JENIS_LAYANAN)));
                                item.setHarga(record.get(Tables.PESANAN_SATUAN.HARGA));
                                item.setQty(record.get(Tables.PESANAN_SATUAN.QTY));
                                return item;
                            })
                            .collect(Collectors.toList());

                    // Menyisipkan detail list ke masing-masing OrderUnitResponse
                    detailItems.forEach(item -> item.setDetails(Collections.singletonList(detailResponse)));

                    return detailResponse;
                })
                .collect(Collectors.toList());
    }





//    public OrderUnitResponse create(DetailOrderUnitRequest request, List<OrderUnitRequest> detailRequests) {
//        String idPesananSatuan = UuidCreator.getTimeOrderedEpoch().toString();
//        LocalDateTime now = LocalDateTime.now();
//
//        // 1. Validasi customer
//        var customer = jooq.selectFrom(Tables.CUSTOMER)
//                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
//                .fetchOne();
//
//        if (customer == null) {
//            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
//        }
//
//        // 2. Hitung harga total (misalnya harga cucian satuan, kamu bisa sesuaikan logika ini)
//        // Contoh hitung harga total dari detail (dummy: 10000 per item), sesuaikan dengan HargaCucianSatuan atau lainnya
////        double hargaTotal = detailRequests.stream()
////                .mapToDouble(d -> HargaCucianSatuan.hitungHarga(d.getKategoriBarang(), d.getUkuran(), d.getJenisLayanan()))
////                .sum();
//
//        // 3. Simpan data pesanan satuan
//        var newOrder = jooq.newRecord(Tables.PESANAN_SATUAN);
//        newOrder.setIdPesananSatuan(idPesananSatuan);
//        newOrder.setIdCustomer(request.getIdCustomer());
//        newOrder.setNoFaktur(InvoiceGenerator.generateNoFaktur());
//        newOrder.setTipePembayaran(PesananSatuanTipePembayaran.lookupLiteral(request.getTipePembayaran()));
//        newOrder.setStatusBayar(PesananSatuanStatusBayar.lookupLiteral(request.getStatusBayar()));
//        newOrder.setStatusOrder(PesananSatuanStatusOrder.lookupLiteral(request.getStatusOrder()));
//        newOrder.setTglMasuk(now);
//        newOrder.setTglSelesai(request.getTglSelesai());
//        newOrder.setCatatan(request.getCatatan());
//        newOrder.setDeletedAt(null);
//        newOrder.setTotalHarga(hargaTotal); // simpan harga total
//        newOrder.store();
//
//        // 4. Simpan detail pesanan satuan
//        for (OrderUnitRequest detail : detailRequests) {
//            var detailRecord = jooq.newRecord(Tables.DETAIL_PESANAN_SATUAN);
//            detailRecord.setIdDetail(UuidCreator.getTimeOrderedEpoch().toString());
//            detailRecord.setIdPesananSatuan(idPesananSatuan);
//            detailRecord.setKategoriBarang(DetailPesananSatuanKategoriBarang.lookupLiteral(detail.getKategoriBarang()));
//            detailRecord.setUkuran(DetailPesananSatuanUkuran.lookupLiteral(detail.getUkuran()));
//            detailRecord.setJenisLayanan(DetailPesananSatuanJenisLayanan.lookupLiteral(detail.getJenisLayanan()));
//            detailRecord.store();
//        }
//
//        // 5. Kembalikan response sesuai constructor yang ada
//        return new DetailOrderUnitResponse(
//                newOrder.getIdPesananSatuan(),
//                newOrder.getIdCustomer(),
//                newOrder.getNoFaktur(),
//                customer.getNama(),
//                customer.getNoTelp(),
//                String.valueOf(newOrder.getTipePembayaran()),
//                String.valueOf(newOrder.getStatusBayar()),
//                String.valueOf(newOrder.getStatusOrder()),
//                newOrder.getTglMasuk(),
//                newOrder.getTglSelesai(),
//                newOrder.getCatatan(),
//                newOrder.getDeletedAt(),
//                newOrder.getTotalHarga()
//        );
//    }



    public OrderUnitResponse createOrderUnit(OrderUnitRequest request) {
        // Step 1: Generate ID dan timestamp
        String orderUnitId = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();

        // Step 2: Validasi input - cek detail pesanan tidak kosong
        if (request.getDetails() == null || request.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Detail pesanan tidak boleh kosong");
        }

        // Step 3: Ambil idCustomer dari detail pertama
        String idCustomer = request.getDetails().get(0).getIdCustomer();

        // Step 4: Cek apakah customer ada di database
        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(idCustomer))
                .fetchOne();
        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + idCustomer + " tidak ditemukan.");
        }

        // Step 5: Validasi enum kategori, ukuran, dan jenis layanan
        var kategori = EnumValidator.validateEnum(
                PesananSatuanKategoriBarang.class,
                request.getKategoriBarang(),
                "Kategori Barang"
        );
        var ukuran = EnumValidator.validateEnum(
                PesananSatuanUkuran.class,
                request.getUkuran(),
                "Ukuran"
        );
        var jenisLayanan = EnumValidator.validateEnum(
                PesananSatuanJenisLayanan.class,
                request.getJenisLayanan(),
                "Jenis Layanan"
        );

        // **Hitung harga otomatis menggunakan kelas HargaCucianSatuan**
        double hargaHitung = HargaCucianSatuan.hitungHarga(kategori, ukuran, jenisLayanan);

        // Step 6: Persiapkan list untuk menampung response detail dan variabel untuk FK di pesanan_satuan
        List<DetailOrderUnitResponse> detailResponses = new ArrayList<>();
        String detailIdForOrderUnit = null;

        // Step 7: Simpan setiap detail pesanan satuan ke database
        for (DetailOrderUnitRequest detailReq : request.getDetails()) {
            // Generate ID untuk detail dan nomor faktur
            String detailId = UuidCreator.getTimeOrderedEpoch().toString();
            String noFaktur = InvoiceGenerator.generateNoFaktur();

            // Validasi enum tipe pembayaran, status bayar, dan status order pada detail
            var tipePembayaran = EnumValidator.validateEnum(
                    DetailPesananSatuanTipePembayaran.class,
                    detailReq.getTipePembayaran(),
                    "Tipe Pembayaran"
            );
            var statusBayar = EnumValidator.validateEnum(
                    DetailPesananSatuanStatusBayar.class,
                    detailReq.getStatusBayar(),
                    "Status Bayar"
            );
            var statusOrder = EnumValidator.validateEnum(
                    DetailPesananSatuanStatusOrder.class,
                    detailReq.getStatusOrder(),
                    "Status Order"
            );

            // Buat record baru detail pesanan satuan
            DetailPesananSatuanRecord detailRecord = jooq.newRecord(Tables.DETAIL_PESANAN_SATUAN);
            detailRecord.setIdDetail(detailId);
            detailRecord.setIdCustomer(detailReq.getIdCustomer());
            detailRecord.setNoFaktur(noFaktur);
            detailRecord.setTipePembayaran(tipePembayaran);
            detailRecord.setStatusBayar(statusBayar);
            detailRecord.setStatusOrder(statusOrder);
            detailRecord.setTglMasuk(now);
            detailRecord.setTglSelesai(null);
            detailRecord.setCatatan(detailReq.getCatatan());
            detailRecord.setDeletedAt(null);
            detailRecord.store();

            // Set FK detail pertama ke order unit
            if (detailIdForOrderUnit == null) {
                detailIdForOrderUnit = detailId;
            }

            // Tambahkan response detail ke list
            detailResponses.add(new DetailOrderUnitResponse(
                    detailId,
                    detailReq.getIdCustomer(),
                    noFaktur,
                    customer.getNama(),
                    customer.getNoTelp(),
                    detailReq.getTipePembayaran(),
                    detailReq.getStatusBayar(),
                    detailReq.getStatusOrder(),
                    detailRecord.getTglMasuk(),
                    detailRecord.getTglSelesai(),
                    detailRecord.getCatatan(),
                    detailRecord.getDeletedAt()
            ));
        }

        // Step 8: Simpan data pesanan satuan (order unit) dengan FK ke detail pesanan
        PesananSatuanRecord orderUnitRecord = jooq.newRecord(Tables.PESANAN_SATUAN);
        orderUnitRecord.setIdPesananSatuan(orderUnitId);
        orderUnitRecord.setIdDetail(detailIdForOrderUnit);
        orderUnitRecord.setKategoriBarang(kategori);
        orderUnitRecord.setUkuran(ukuran);
        orderUnitRecord.setJenisLayanan(jenisLayanan);
        orderUnitRecord.setHarga(hargaHitung * request.getQty()); // Harga sudah dihitung otomatis
        orderUnitRecord.setQty(request.getQty());
        orderUnitRecord.store();

        // Step 9: Bangun dan kembalikan response lengkap
        return new OrderUnitResponse(
                orderUnitRecord.getIdPesananSatuan(),
                orderUnitRecord.getIdDetail(),
                detailResponses,
                orderUnitRecord.getKategoriBarang().getLiteral(),
                orderUnitRecord.getUkuran().getLiteral(),
                orderUnitRecord.getJenisLayanan().getLiteral(),
                orderUnitRecord.getHarga(),
                orderUnitRecord.getQty()
        );
    }


}
