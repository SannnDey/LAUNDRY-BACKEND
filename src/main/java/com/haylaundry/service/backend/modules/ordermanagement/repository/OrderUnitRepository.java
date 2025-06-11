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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderUnitRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    @Inject
    private DailyIncomeService dailyIncomeService;

    public List<DetailOrderUnitResponse> getAllOrderUnit() {
        DecimalFormat formatter = new DecimalFormat("#,###");

        List<Record> records = jooq.select()
                .from(Tables.DETAIL_PESANAN_SATUAN)
                .join(Tables.ITEM_PESANAN_SATUAN)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL))
                .leftJoin(Tables.CUSTOMER)
                .on(Tables.DETAIL_PESANAN_SATUAN.ID_CUSTOMER.eq(Tables.CUSTOMER.ID_CUSTOMER))
                .where(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT.isNull())
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
                    detailResponse.setTipePembayaran(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TIPE_PEMBAYARAN)).replace("_", " "));
                    detailResponse.setStatusBayar(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_BAYAR)).replace("_", " "));
                    detailResponse.setStatusOrder(String.valueOf(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.STATUS_ORDER)).replace("_", " "));

                    double totalHarga = firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA);
                    String formattedTotalHarga = formatter.format(totalHarga);

                    detailResponse.setTotalHarga(formattedTotalHarga);
                    detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
                    detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
                    detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
                    detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

                    List<OrderUnitResponse> detailItems = groupRecords.stream()
                            .map(record -> {
                                OrderUnitResponse item = new OrderUnitResponse();
                                item.setIdPesananSatuan(record.get(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN));
                                item.setIdDetail(record.get(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL));
                                item.setKategoriBarang(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.KATEGORI_BARANG)).replace("_", " "));
                                item.setUkuran(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.UKURAN)).replace("_", " "));
                                item.setJenisLayanan(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.JENIS_LAYANAN)).replace("_", " "));

                                double harga = record.get(Tables.ITEM_PESANAN_SATUAN.HARGA);
                                String formattedHarga = formatter.format(harga);

                                item.setHarga(formattedHarga);
                                item.setQty(record.get(Tables.ITEM_PESANAN_SATUAN.QTY));
                                return item;
                            })
                            .collect(Collectors.toList());

                    detailResponse.setItems(detailItems);

                    return detailResponse;
                })
                .collect(Collectors.toList());
    }



    public DetailOrderUnitResponse getOrderUnitById(String idDetail) {
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

        DecimalFormat formatter = new DecimalFormat("#,###");

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

        double totalHarga = firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA);
        String formattedTotalHarga = formatter.format(totalHarga);

        detailResponse.setTotalHarga(formattedTotalHarga);

        detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
        detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
        detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
        detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

        List<OrderUnitResponse> items = records.stream().map(record -> {
            OrderUnitResponse item = new OrderUnitResponse();
            item.setIdPesananSatuan(record.get(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN));
            item.setIdDetail(record.get(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL));
            item.setKategoriBarang(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.KATEGORI_BARANG)).replace("_", " "));
            item.setUkuran(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.UKURAN)).replace("_", " "));
            item.setJenisLayanan(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.JENIS_LAYANAN)).replace("_", " "));

            double harga = record.get(Tables.ITEM_PESANAN_SATUAN.HARGA);
            String formattedHarga = formatter.format(harga);  // Format harga ke string dengan pemisah ribuan

            item.setHarga(formattedHarga);  // Set formatted harga as String
            item.setQty(record.get(Tables.ITEM_PESANAN_SATUAN.QTY));
            return item;
        }).collect(Collectors.toList());

        detailResponse.setItems(items);

        return detailResponse;
    }


    public DetailOrderUnitResponse getOrderUnitByNoFaktur(String nomor) {
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

        DecimalFormat formatter = new DecimalFormat("#,###");

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

        double totalHarga = firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TOTAL_HARGA);
        String formattedTotalHarga = formatter.format(totalHarga);

        detailResponse.setTotalHarga(formattedTotalHarga);

        detailResponse.setTglMasuk(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_MASUK));
        detailResponse.setTglSelesai(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.TGL_SELESAI));
        detailResponse.setCatatan(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.CATATAN));
        detailResponse.setDeletedAt(firstRecord.get(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT));

        List<OrderUnitResponse> items = records.stream().map(record -> {
            OrderUnitResponse item = new OrderUnitResponse();
            item.setIdPesananSatuan(record.get(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN));
            item.setIdDetail(record.get(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL));
            item.setKategoriBarang(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.KATEGORI_BARANG)).replace("_", " "));
            item.setUkuran(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.UKURAN)).replace("_", " "));
            item.setJenisLayanan(String.valueOf(record.get(Tables.ITEM_PESANAN_SATUAN.JENIS_LAYANAN)).replace("_", " "));

            double harga = record.get(Tables.ITEM_PESANAN_SATUAN.HARGA);
            String formattedHarga = formatter.format(harga);

            item.setHarga(formattedHarga);
            item.setQty(record.get(Tables.ITEM_PESANAN_SATUAN.QTY));
            return item;
        }).collect(Collectors.toList());

        // Set items ke dalam response
        detailResponse.setItems(items);

        return detailResponse;
    }




    public DetailOrderUnitResponse createOrderUnit(DetailOrderUnitRequest request) {
        LocalDateTime now = LocalDateTime.now();

        var customer = jooq.selectFrom(Tables.CUSTOMER)
                .where(Tables.CUSTOMER.ID_CUSTOMER.eq(request.getIdCustomer()))
                .fetchOne();

        if (customer == null) {
            throw new IllegalArgumentException("Customer dengan ID " + request.getIdCustomer() + " tidak ditemukan.");
        }

        String detailId = UuidCreator.getTimeOrderedEpoch().toString();
        String noFaktur = InvoiceGenerator.generateNoFaktur();

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

        DetailPesananSatuanRecord detailRecord = jooq.newRecord(Tables.DETAIL_PESANAN_SATUAN);
        detailRecord.setIdDetail(detailId);
        detailRecord.setIdCustomer(request.getIdCustomer());
        detailRecord.setNoFaktur(noFaktur);
        detailRecord.setTipePembayaran(tipePembayaran);
        detailRecord.setStatusBayar(statusBayar);
        detailRecord.setStatusOrder(statusOrder);
        detailRecord.setTotalHarga(0.0);
        detailRecord.setTglMasuk(request.getTglMasuk() != null ? request.getTglMasuk() : now);
        detailRecord.setTglSelesai(request.getTglSelesai());
        detailRecord.setCatatan(request.getCatatan());
        detailRecord.setDeletedAt(request.getDeletedAt());
        detailRecord.store();

        List<OrderUnitResponse> orderUnitResponses = new ArrayList<>();

        double totalHarga = 0.0;

        DecimalFormat formatter = new DecimalFormat("#,###");

        for (OrderUnitRequest item : request.getItems()) {
            // Validasi qty minimal 1
            if (item.getQty() < 1) {
                throw new IllegalArgumentException("Quantity untuk item kategori " + item.getKategoriBarang() + " minimal 1.");
            }

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

            double hargaHitung = PriceOrderUnit.hitungHarga(kategori, ukuran, jenisLayanan);
            double hargaFinal = hargaHitung * item.getQty();
            totalHarga += hargaFinal;
            String formattedHargaFinal = formatter.format(hargaFinal);

            ItemPesananSatuanRecord orderUnitRecord = jooq.newRecord(Tables.ITEM_PESANAN_SATUAN);
            orderUnitRecord.setIdItemSatuan(UuidCreator.getTimeOrderedEpoch().toString());
            orderUnitRecord.setIdDetail(detailId); // FK ke detail pesanan yang sudah ada
            orderUnitRecord.setKategoriBarang(kategori);
            orderUnitRecord.setUkuran(ukuran);
            orderUnitRecord.setJenisLayanan(jenisLayanan);
            orderUnitRecord.setHarga(hargaFinal);
            orderUnitRecord.setQty(item.getQty());
            orderUnitRecord.store();

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

        String formattedTotalHarga = formatter.format(totalHarga);
        detailRecord.setTotalHarga(totalHarga);

        if (DetailPesananSatuanStatusBayar.Lunas.equals(detailRecord.getStatusBayar())) {
            dailyIncomeService.createLaporan(detailRecord.getTglMasuk().toLocalDate());
        } else if (DetailPesananSatuanStatusBayar.Belum_Lunas.equals(detailRecord.getStatusBayar())) {
            dailyIncomeService.createLaporan(detailRecord.getTglMasuk().toLocalDate());
        }

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


    public OrderUnitStatusBayar updateStatusBayar(String idDetail, String statusBayar) {
        DetailPesananSatuanRecord orderUnitToUpdate = jooq.selectFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .fetchOne();

        if (orderUnitToUpdate == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        DetailPesananSatuanStatusBayar status = DetailPesananSatuanStatusBayar.lookupLiteral(statusBayar);

        if (status == null) {
            throw new IllegalArgumentException("Status bayar tidak valid: " + statusBayar);
        }

        orderUnitToUpdate.setStatusBayar(status);
        orderUnitToUpdate.store();

        if (DetailPesananSatuanStatusBayar.Lunas.equals(status)) {
            dailyIncomeService.createLaporan(orderUnitToUpdate.getTglMasuk().toLocalDate());
        } else if (DetailPesananSatuanStatusBayar.Belum_Lunas.equals(status)) {
            dailyIncomeService.createLaporan(orderUnitToUpdate.getTglMasuk().toLocalDate());
        }

        return new OrderUnitStatusBayar(
                orderUnitToUpdate.getIdDetail(),
                orderUnitToUpdate.getNoFaktur(),
                status.getLiteral()
        );
    }

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
        int deletedItems = jooq.deleteFrom(Tables.ITEM_PESANAN_SATUAN)
                .where(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        int deletedParent = jooq.deleteFrom(Tables.DETAIL_PESANAN_SATUAN)
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        return deletedParent > 0;
    }


    public boolean softDeleteOrderUnitById(String idDetail) {
        int updatedItems = jooq.update(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.ITEM_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        int updatedParent = jooq.update(Tables.DETAIL_PESANAN_SATUAN)
                .set(Tables.DETAIL_PESANAN_SATUAN.DELETED_AT, LocalDateTime.now())  // Mengatur waktu soft delete
                .where(Tables.DETAIL_PESANAN_SATUAN.ID_DETAIL.eq(idDetail))
                .execute();

        return updatedItems > 0 && updatedParent > 0;
    }


}
