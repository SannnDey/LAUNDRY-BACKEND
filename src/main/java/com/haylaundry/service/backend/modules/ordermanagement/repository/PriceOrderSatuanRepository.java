package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.PriceOrderSatuanRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PriceOrderSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.PriceOrderSatuanResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PriceOrderSatuanRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    public Optional<PriceOrderSatuanResponse> getPriceById(String idItems) {
        return jooq.selectFrom(Tables.PRICE_ORDER_SATUAN)
                .where(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN.eq(idItems))
                .fetchOptional(record -> {
                    PriceOrderSatuanResponse resp = new PriceOrderSatuanResponse();
                    resp.setIdPriceSatuan(record.getIdPriceSatuan());
                    resp.setHargaPerItem(record.getHargaPerItem()); // langsung Double
                    resp.setKategoriBarang(record.getKategoriBarang());
                    resp.setUkuran(record.getUkuran());
                    resp.setJenisLayanan(record.getJenisLayanan());
                    return resp;
                });
    }

    // 🔹 Get semua data price order satuan
    public List<PriceOrderSatuanResponse> getAll() {
        return jooq.selectFrom(Tables.PRICE_ORDER_SATUAN)
                .fetch(record -> {
                    PriceOrderSatuanResponse resp = new PriceOrderSatuanResponse();
                    resp.setIdPriceSatuan(record.getIdPriceSatuan());
                    resp.setHargaPerItem(record.getHargaPerItem()); // langsung Double
                    resp.setKategoriBarang(record.getKategoriBarang());
                    resp.setUkuran(record.getUkuran());
                    resp.setJenisLayanan(record.getJenisLayanan());
                    return resp;
                });
    }

    public String createPrice(PriceOrderSatuanRequest request) {
        Optional<PriceOrderSatuanRecord> existing = jooq.selectFrom(Tables.PRICE_ORDER_SATUAN)
                .where(Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG.eq(request.getKategoriBarang()))
                .and(Tables.PRICE_ORDER_SATUAN.UKURAN.eq(request.getUkuran()))
                .and(Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN.eq(request.getJenisLayanan()))
                .fetchOptional();

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Harga untuk kombinasi kategori, ukuran, dan layanan ini sudah ada.");
        }

        String idPrice = UuidCreator.getTimeOrderedEpoch().toString();

        jooq.insertInto(Tables.PRICE_ORDER_SATUAN)
                .set(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN, idPrice)
                .set(Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG, request.getKategoriBarang())
                .set(Tables.PRICE_ORDER_SATUAN.UKURAN, request.getUkuran())
                .set(Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN, request.getJenisLayanan())
                .set(Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM, request.getHargaPerItem())
                .execute();

        return idPrice;
    }

    public void updatePrice(String idPriceSatuan, PriceOrderSatuanRequest request) {
        int updated = jooq.update(Tables.PRICE_ORDER_SATUAN)
                .set(Tables.PRICE_ORDER_SATUAN.KATEGORI_BARANG, request.getKategoriBarang())
                .set(Tables.PRICE_ORDER_SATUAN.UKURAN, request.getUkuran())
                .set(Tables.PRICE_ORDER_SATUAN.HARGA_PER_ITEM, request.getHargaPerItem())
                .set(Tables.PRICE_ORDER_SATUAN.JENIS_LAYANAN, request.getJenisLayanan())
                .where(Tables.PRICE_ORDER_SATUAN.ID_PRICE_SATUAN.eq(idPriceSatuan))
                .execute();

        if (updated == 0) {
            throw new IllegalArgumentException("Data dengan ID " + idPriceSatuan + " tidak ditemukan.");
        }
    }

}
