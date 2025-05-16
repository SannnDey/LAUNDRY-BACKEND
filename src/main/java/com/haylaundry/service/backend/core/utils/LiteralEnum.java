package com.haylaundry.service.backend.core.utils;

import org.jooq.EnumType;

public class LiteralEnum {
    public static <E extends Enum<E> & EnumType> E lookup(Class<E> enumClass, String value) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getLiteral().equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
