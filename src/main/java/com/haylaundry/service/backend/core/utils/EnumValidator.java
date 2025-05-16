package com.haylaundry.service.backend.core.utils;

import org.jooq.EnumType;

public class EnumValidator {
    public static <E extends Enum<E> & EnumType> E validateEnum(Class<E> enumClass, String literal, String fieldName) {
        E result = LiteralEnum.lookup(enumClass, literal);
        if (result == null) {
            throw new IllegalArgumentException(fieldName + " tidak valid: " + literal);
        }
        return result;
    }
}

