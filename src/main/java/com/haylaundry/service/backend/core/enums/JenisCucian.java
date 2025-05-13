package com.haylaundry.service.backend.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JenisCucian {
    KOMPLIT("Komplit"),
    CUCI_LIPAT("Cuci Lipat"),
    SETRIKA("Setrika");

    private final String value;

    JenisCucian(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JenisCucian fromValue(String value) {
        for (JenisCucian jenis : JenisCucian.values()) {
            if (jenis.value.equalsIgnoreCase(value)) {
                return jenis;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
