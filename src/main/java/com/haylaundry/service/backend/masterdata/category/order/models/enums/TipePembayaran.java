package com.haylaundry.service.backend.masterdata.category.order.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipePembayaran {
    CASH("Cash"),
    QRIS("QRIS");

    private final String value;

    TipePembayaran(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TipePembayaran fromValue(String value) {
        for (TipePembayaran tipe : TipePembayaran.values()) {
            if (tipe.value.equalsIgnoreCase(value)) {
                return tipe;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
