package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.jooq.gen.tables.records.PriceOrderRecord;
import com.haylaundry.service.backend.modules.ordermanagement.price.response.PriceOrderResponse;
import com.haylaundry.service.backend.modules.ordermanagement.repository.PriceOrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

@ApplicationScoped
public class PriceOrder {
    @Inject
    private DSLContext jooq;
    @Inject
    PriceOrderRepository priceOrderRepository;

//    public double getHargaPerKg(PesananTipeCucian tipeCucian, PesananJenisCucian jenisCucian) {
//        PriceOrderResponse harga = priceOrderRepository.getHargaByTipeAndJenis(
//                tipeCucian.name(), jenisCucian.name()
//        );
//
//        if (harga == null) {
//            throw new IllegalArgumentException("Harga belum tersedia untuk tipe " + tipeCucian + " dan jenis " + jenisCucian);
//        }
//
//        return harga.getHargaperKg();
//    }

    public double hitungHargaTotal(String tipeCucian, String jenisCucian, double berat) {
        PriceOrderRecord record = jooq.selectFrom(Tables.PRICE_ORDER)
                .where(Tables.PRICE_ORDER.TIPE_CUCIAN.eq(tipeCucian))
                .and(Tables.PRICE_ORDER.JENIS_CUCIAN.eq(jenisCucian))
                .fetchOne();

        if (record == null) {
            throw new IllegalArgumentException("Harga belum tersedia untuk tipe " +
                    tipeCucian + " dan jenis " + jenisCucian);
        }

        return record.getHargaPerKg() * berat;
    }



}
