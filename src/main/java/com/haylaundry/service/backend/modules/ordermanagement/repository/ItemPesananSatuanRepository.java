package com.haylaundry.service.backend.modules.ordermanagement.repository;

import com.github.f4b6a3.uuid.UuidCreator;
import com.haylaundry.service.backend.core.orm.JooqRepository;
import com.haylaundry.service.backend.jooq.gen.Tables;
import com.haylaundry.service.backend.modules.ordermanagement.models.request.orderunit.ItemPesananSatuanRequest;
import com.haylaundry.service.backend.modules.ordermanagement.models.response.orderunit.ItemPesananSatuanResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ItemPesananSatuanRepository extends JooqRepository {

    @Inject
    private DSLContext jooq;

    public String createItemWithoutPesanan(String idPriceSatuan, ItemPesananSatuanRequest request, double hargaSatuan) {
        double subtotal = hargaSatuan * request.getQty();
        String idItem = UuidCreator.getTimeOrderedEpoch().toString();

        jooq.insertInto(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN, idItem)
                .set(Tables.ITEM_PESANAN_SATUAN.ID_PRICE_SATUAN, idPriceSatuan)
                .set(Tables.ITEM_PESANAN_SATUAN.QTY, request.getQty())
                // ID_PESANAN_SATUAN tidak di-set → null dulu
                .execute();

        return idItem;
    }


    public List<ItemPesananSatuanResponse> getByPesananId(String idPesanan) {
        return jooq.selectFrom(Tables.ITEM_PESANAN_SATUAN)
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesanan))
                .and(Tables.ITEM_PESANAN_SATUAN.DELETED_AT.isNull())
                .fetchInto(ItemPesananSatuanResponse.class);
    }


    public void assignItemsToPesanan(List<String> idItemList, String idPesanan) {
        jooq.update(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN, idPesanan)
                .where(Tables.ITEM_PESANAN_SATUAN.ID_ITEM_SATUAN.in(idItemList))
                .execute();
    }


    public int softDeleteByPesananId(String idPesanan) {
        return jooq.update(Tables.ITEM_PESANAN_SATUAN)
                .set(Tables.ITEM_PESANAN_SATUAN.DELETED_AT, LocalDateTime.from(Instant.now()))
                .where(Tables.ITEM_PESANAN_SATUAN.ID_PESANAN_SATUAN.eq(idPesanan))
                .execute();
    }
}