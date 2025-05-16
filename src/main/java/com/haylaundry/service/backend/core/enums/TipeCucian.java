package com.haylaundry.service.backend.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipeCucian {
    SUPER_EXPRESS_3_JAM_KOMPLIT("Super Express 3 Jam Komplit"),
    EXPRESS_1_HARI("Express 1 Hari"),
    STANDAR_2_HARI("Standar 2 Hari"),
    REGULER_3_HARI("Reguler 3 Hari");

    private final String value;

    TipeCucian(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TipeCucian fromValue(String value) {
        for (TipeCucian tipe : TipeCucian.values()) {
            if (tipe.value.equalsIgnoreCase(value)) {
                return tipe;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
