package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.core.utils.HargaCucianSatuan;
import com.haylaundry.service.backend.core.utils.InvoiceGenerator;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.enums.*;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.DetailOrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.OrderUnitRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.DetailOrderUnitResponse;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.OrderUnitResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderUnitRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    public List<OrderUnitResponse> GetAllOrderUnitsWithDetails() {
        List<Record> records = jooq.select()
                .from(Tables.PESANAN_SATUAN)
                .join(Tables.DETAIL_PESANAN_SATUAN)
                .on(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(Tables.DETAIL_PESANAN_SATUAN.ID_PESANAN_SATUAN))
                .leftJoin(Tables.CUSTOMER)
                .on(Tables.PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .fetch();

        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN),
                        Collectors.mapping(record -> record, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> {
                    Record firstRecord = entry.getValue().get(0);
                    OrderUnitResponse orderUnit = new OrderUnitResponse();
                    orderUnit.setIdPesananSatuan(firstRecord.get(Tables.PESANAN_SATUAN.ID_PESANAN_SATUAN));
                    orderUnit.setIdCustomer(firstRecord.get(Tables.CUSTOMER.ID_CUSTOMER));
                    orderUnit.setNoFaktur(firstRecord.get(Tables.PESANAN_SATUAN.NO_FAKTUR));
                    orderUnit.setNamaCustomer(firstRecord.get(Tables.CUSTOMER.NAMA));
                    orderUnit.setCustomerPhone(firstRecord.get(Tables.CUSTOMER.NO_TELP));
                    orderUnit.setTipePembayaran(String.valueOf(firstRecord.get(Tables.PESANAN_SATUAN.TIPE_PEMBAYARAN)));
                    orderUnit.setStatusBayar(String.valueOf(firstRecord.get(Tables.PESANAN_SATUAN.STATUS_BAYAR)));
                    orderUnit.setStatusOrder(String.valueOf(firstRecord.get(Tables.PESANAN_SATUAN.STATUS_ORDER)));
                    orderUnit.setTglMasuk(firstRecord.get(Tables.PESANAN_SATUAN.TGL_MASUK));
                    orderUnit.setTglSelesai(firstRecord.get(Tables.PESANAN_SATUAN.TGL_SELESAI));
                    orderUnit.setCatatan(firstRecord.get(Tables.PESANAN_SATUAN.CATATAN));
                    orderUnit.setDeletedAt(firstRecord.get(Tables.PESANAN_SATUAN.DELETED_AT));

                    List<DetailOrderUnitResponse> details = entry.getValue().stream()
                            .map(record -> {
                                DetailOrderUnitResponse detail = new DetailOrderUnitResponse();
                                detail.setIdDetail(record.get(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL));
                                detail.setIdPesananSatuan(record.get(Tables.DETAIL_PESANAN_SATUAN.ID_PESANAN_SATUAN));
                                detail.setKategoriBarang(String.valueOf(record.get(Tables.DETAIL_PESANAN_SATUAN.KATEGORI_BARANG)));
                                detail.setUkuran(String.valueOf(record.get(Tables.DETAIL_PESANAN_SATUAN.UKURAN)));
                                detail.setJenisLayanan(String.valueOf(record.get(Tables.DETAIL_PESANAN_SATUAN.JENIS_LAYANAN)));
                                return detail;
                            })
                            .collect(Collectors.toList());

                    // Anda dapat menambahkan setter untuk detail jika OrderUnitResponse memiliki field tersebut
                    // orderUnit.setDetails(details);

                    return orderUnit;
                })
                .collect(Collectors.toList());
    }

    public OrderUnitResponse create(OrderUnitRequest request, List<DetailOrderUnitRequest> detailRequests) {
        String idPesananSatuan = UuidCreator.getTimeOrderedEpoch().toString();
        LocalDateTime now = LocalDateTime.now();

        // 1. Validasi customer
        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        // 2. Hitung harga total (misalnya harga cucian satuan, kamu bisa sesuaikan logika ini)
        // Contoh hitung harga total dari detail (dummy: 10000 per item), sesuaikan dengan HargaCucianSatuan atau lainnya
        double hargaTotal = detailRequests.stream()
                .mapToDouble(d -> HargaCucianSatuan.hitungHarga(d.getKategoriBarang(), d.getUkuran(), d.getJenisLayanan()))
                .sum();

        // 3. Simpan data pesanan satuan
        var newOrder = jooq.newRecord(Tables.PESANAN_SATUAN);
        newOrder.setIdPesananSatuan(idPesananSatuan);
        newOrder.setIdCustomer(request.getIdCustomer());
        newOrder.setNoFaktur(InvoiceGenerator.generateNoFaktur());
        newOrder.setTipePembayaran(PesananSatuanTipePembayaran.lookupLiteral(request.getTipePembayaran()));
        newOrder.setStatusBayar(PesananSatuanStatusBayar.lookupLiteral(request.getStatusBayar()));
        newOrder.setStatusOrder(PesananSatuanStatusOrder.lookupLiteral(request.getStatusOrder()));
        newOrder.setTglMasuk(now);
        newOrder.setTglSelesai(request.getTglSelesai());
        newOrder.setCatatan(request.getCatatan());
        newOrder.setDeletedAt(null);
        newOrder.setTotalHarga(hargaTotal); // simpan harga total
        newOrder.store();

        // 4. Simpan detail pesanan satuan
        for (DetailOrderUnitRequest detail : detailRequests) {
            var detailRecord = jooq.newRecord(Tables.DETAIL_PESANAN_SATUAN);
            detailRecord.setIdDetail(UuidCreator.getTimeOrderedEpoch().toString());
            detailRecord.setIdPesananSatuan(idPesananSatuan);
            detailRecord.setKategoriBarang(DetailPesananSatuanKategoriBarang.lookupLiteral(detail.getKategoriBarang()));
            detailRecord.setUkuran(DetailPesananSatuanUkuran.lookupLiteral(detail.getUkuran()));
            detailRecord.setJenisLayanan(DetailPesananSatuanJenisLayanan.lookupLiteral(detail.getJenisLayanan()));
            detailRecord.store();
        }

        // 5. Kembalikan response sesuai constructor yang ada
        return new OrderUnitResponse(
                newOrder.getIdPesananSatuan(),
                newOrder.getIdCustomer(),
                newOrder.getNoFaktur(),
                customer.getNama(),
                customer.getNoTelp(),
                String.valueOf(newOrder.getTipePembayaran()),
                String.valueOf(newOrder.getStatusBayar()),
                String.valueOf(newOrder.getStatusOrder()),
                newOrder.getTglMasuk(),
                newOrder.getTglSelesai(),
                newOrder.getCatatan(),
                newOrder.getDeletedAt(),
                newOrder.getTotalHarga()
        );
    }

}
