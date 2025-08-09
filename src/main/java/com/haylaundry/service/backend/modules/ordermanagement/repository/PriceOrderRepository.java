package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.PriceOrderSatuan;
import com.haylaundry.service.backend.jooq.gen.tables.records.PriceOrderRecord;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.PriceOrderSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.price.request.PriceOrderRequest;
import com.haylaundry.service.backend.modules.ordermanagement.price.response.PriceOrderResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import java.text.DecimalFormat;


import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PriceOrderRepository extends JooqRepository {
    @Inject
    private DSLContext jooq;

    private String formatHarga(Double harga) {
        if (harga == null) return "0";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // ribuan pakai titik
        symbols.setDecimalSeparator(',');  // desimal pakai koma

        DecimalFormat formatter = new DecimalFormat("#,###.##", symbols);
        return formatter.format(harga);
    }



    public void upsertHarga(String tipeCucian, String jenisCucian, Double hargaPerKg) {
        if (tipeCucian == null || tipeCucian.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipe cucian tidak boleh kosong");
        }
        if (jenisCucian == null || jenisCucian.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis cucian tidak boleh kosong");
        }

        jooq.insertInto(Tables.PRICE_ORDER)
                .set(Tables.PRICE_ORDER.ID_PRICE, UuidCreator.getTimeOrderedEpoch().toString())
                .set(Tables.PRICE_ORDER.TIPE_CUCIAN, tipeCucian)
                .set(Tables.PRICE_ORDER.JENIS_CUCIAN, jenisCucian)
                .set(Tables.PRICE_ORDER.HARGA_PER_KG, hargaPerKg)
                .set(Tables.PRICE_ORDER.UPDATED_AT, LocalDateTime.now())
                .onConflict(Tables.PRICE_ORDER.TIPE_CUCIAN, Tables.PRICE_ORDER.JENIS_CUCIAN)
                .doUpdate()
                .set(Tables.PRICE_ORDER.HARGA_PER_KG, hargaPerKg)
                .set(Tables.PRICE_ORDER.UPDATED_AT, LocalDateTime.now())
                .execute();
    }




    // ✅ Get semua harga
    public List<PriceOrderResponse> getAllPrices() {
        return jooq.selectFrom(Tables.PRICE_ORDER)
                .fetch(record -> new PriceOrderResponse(
                        record.getIdPrice(),
                        String.valueOf(record.get(Tables.PRICE_ORDER.TIPE_CUCIAN)),
                        String.valueOf(record.get(Tables.PRICE_ORDER.JENIS_CUCIAN)),
                        formatHarga(record.getHargaPerKg())
                ));
    }



    // ✅ Get harga berdasarkan tipe dan jenis cucian (String, tanpa enum)
    public PriceOrderResponse getHargaByTipeAndJenis(String tipeCucian, String jenisCucian) {
        PriceOrderRecord record = jooq.selectFrom(Tables.PRICE_ORDER)
                .where(Tables.PRICE_ORDER.TIPE_CUCIAN.eq(tipeCucian))
                .and(Tables.PRICE_ORDER.JENIS_CUCIAN.eq(jenisCucian))
                .fetchOne();

        if (record == null) {
            return null;
        }

        return new PriceOrderResponse(
                record.getIdPrice(),
                record.getTipeCucian(), // langsung String
                record.getJenisCucian(), // langsung String
                formatHarga(record.getHargaPerKg())
        );
    }


    public void updateHargaById(String idPrice, Double hargaPerKg) {
        int updated = jooq.update(Tables.PRICE_ORDER)
                .set(Tables.PRICE_ORDER.HARGA_PER_KG, hargaPerKg)
                .set(Tables.PRICE_ORDER.UPDATED_AT, LocalDateTime.now())
                .where(Tables.PRICE_ORDER.ID_PRICE.eq(idPrice))
                .execute();

        if (updated == 0) {
            throw new IllegalArgumentException("Data dengan ID " + idPrice + " tidak ditemukan.");
        }
    }

}
