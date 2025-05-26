package com.haylaundry.service.backend.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    CASH("Cash"),
    QRIS("QRIS");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentType fromValue(String value) {
        for (PaymentType tipe : PaymentType.values()) {
            if (tipe.value.equalsIgnoreCase(value)) {
                return tipe;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
