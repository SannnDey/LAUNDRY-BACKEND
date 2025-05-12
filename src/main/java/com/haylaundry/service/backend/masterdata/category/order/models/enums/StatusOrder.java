package com.haylaundry.service.backend.masterdata.category.order.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusOrder {
    PICKUP("Pickup"),
    CUCI("Cuci"),
    SELESAI("Selesai");

    private final String value;

    StatusOrder(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusOrder fromValue(String value) {
        for (StatusOrder status : StatusOrder.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
