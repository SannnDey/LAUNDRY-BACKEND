package com.haylaundry.service.backend.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusBayar {
    BELUM_LUNAS("Belum Lunas"),
    LUNAS("Lunas");

    private final String value;

    StatusBayar(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StatusBayar fromValue(String value) {
        for (StatusBayar status : StatusBayar.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
