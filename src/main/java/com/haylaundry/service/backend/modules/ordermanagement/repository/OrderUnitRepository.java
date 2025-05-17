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
                    detailResponse.setTipePembayaran(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TIPE_PEMBAYARAN)).replace('_', ' '));
                    detailResponse.setStatusBayar(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR)).replace('_', ' '));
                    detailResponse.setStatusOrder(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_ORDER)).replace('_', ' '));
                    detailResponse.setTotalHarga(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA));
                    detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
                    detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
                    detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
                    detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

                    List<OrderUnitResponse> detailItems = groupRecords.stream()
                            .map(record -> {
                                OrderUnitResponse item = new OrderUnitResponse();
                                item.setIdPesananSatuan(record.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN));
                                item.setIdDetail(record.get(Tables.PESANAN_SATUAN.ID_DETAIL));
                                item.setKategoriBarang(String.valueOf(record.get(Tables.PESANAN_SATUAN.KATEGORI_BARANG)).replace('_', ' '));
                                item.setUkuran(String.valueOf(record.get(Tables.PESANAN_SATUAN.UKURAN)).replace('_', ' '));
                                item.setJenisLayanan(String.valueOf(record.get(Tables.PESANAN_SATUAN.JENIS_LAYANAN)).replace('_', ' '));
                                item.setHarga(record.get(Tables.PESANAN_SATUAN.HARGA));
                                item.setQty(record.get(Tables.PESANAN_SATUAN.QTY));
                                return item;
                            })
                            .collect(Collectors.toList());

                    // Tambahkan items ke detail response
                    detailResponse.setItems(detailItems);

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



    public DetailOrderUnitResponse createOrderUnit(DetailOrderUnitRequest request) {
        LocalDateTime now = LocalDateTime.now();

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
        detailRecord.setTglMasuk(request.getTglMasuk() != null ? request.getTglMasuk() : now);
        detailRecord.setTglSelesai(request.getTglSelesai());
        detailRecord.setCatatan(request.getCatatan());
        detailRecord.setDeletedAt(request.getDeletedAt());
        detailRecord.store();

        // List untuk menampung response OrderUnitResponse
        List<OrderUnitResponse> orderUnitResponses = new ArrayList<>();

        // Variabel untuk menghitung total harga semua item
        double totalHarga = 0.0;

        // Iterasi setiap item OrderUnitRequest dalam DetailOrderUnitRequest.items
        for (OrderUnitRequest item : request.getItems()) {
            // Validasi qty minimal 1
            if (item.getQty() < 1) {
                throw new IllegalArgumentException("Quantity untuk item kategori " + item.getKategoriBarang() + " minimal 1.");
            }

            // Validasi enum kategori, ukuran, dan jenis layanan di setiap item
            var kategori = EnumValidator.validateEnum(
                    PesananSatuanKategoriBarang.class,
                    item.getKategoriBarang(),
                    "Kategori Barang"
            );
            var ukuran = EnumValidator.validateEnum(
                    PesananSatuanUkuran.class,
                    item.getUkuran(),
                    "Ukuran"
            );
            var jenisLayanan = EnumValidator.validateEnum(
                    PesananSatuanJenisLayanan.class,
                    item.getJenisLayanan(),
                    "Jenis Layanan"
            );

            // Hitung harga otomatis
            double hargaHitung = HargaCucianSatuan.hitungHarga(kategori, ukuran, jenisLayanan);
            double hargaFinal = hargaHitung * item.getQty();

            // Tambahkan ke total harga
            totalHarga += hargaFinal;

            // Simpan PesananSatuan record untuk tiap item (child)
            PesananSatuanRecord orderUnitRecord = jooq.newRecord(Tables.PESANAN_SATUAN);
            orderUnitRecord.setIdPesananSatuan(UuidCreator.getTimeOrderedEpoch().toString());
            orderUnitRecord.setIdDetail(detailId); // FK ke detail pesanan yang sudah ada
            orderUnitRecord.setKategoriBarang(kategori);
            orderUnitRecord.setUkuran(ukuran);
            orderUnitRecord.setJenisLayanan(jenisLayanan);
            orderUnitRecord.setHarga(hargaFinal);
            orderUnitRecord.setQty(item.getQty());
            orderUnitRecord.store();

            // Tambahkan ke response list
            orderUnitResponses.add(new OrderUnitResponse(
                    orderUnitRecord.getIdPesananSatuan(),
                    detailId,
                    kategori.getLiteral(),
                    ukuran.getLiteral(),
                    jenisLayanan.getLiteral(),
                    hargaFinal,
                    orderUnitRecord.getQty()
            ));
        }

        // Update totalHarga di detailRecord setelah insert semua pesanan_satuan
        detailRecord.setTotalHarga(totalHarga);
        detailRecord.store();

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
                totalHarga,
                detailRecord.getTglMasuk(),
                detailRecord.getTglSelesai(),
                detailRecord.getCatatan(),
                detailRecord.getDeletedAt(),
                orderUnitResponses
        );
    }


}
