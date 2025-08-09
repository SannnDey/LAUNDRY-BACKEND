//package com.haylaundry.service.backend.core.utils;
//
//import com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian;
//import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;
//import com.haylaundry.service.backend.jooq.gen.enums.PriceOrderJenisCucian;
//import com.haylaundry.service.backend.jooq.gen.enums.PriceOrderTipeCucian;
//
//public class EnumMapper {
//
//    public static PesananTipeCucian toTipeCucianEnum(String tipe) {
//        if (tipe == null) return null;
//
//        for (PesananTipeCucian value : PesananTipeCucian.values()) {
//            if (value.getLiteral().equalsIgnoreCase(tipe)) {
//                return value;
//            }
//        }
//
//        throw new IllegalArgumentException("Tipe cucian tidak dikenali: " + tipe);
//    }
//
//    public static PesananJenisCucian toJenisCucianEnum(String jenis) {
//        if (jenis == null) return null;
//
//        for (PesananJenisCucian value : PesananJenisCucian.values()) {
//            if (value.getLiteral().equalsIgnoreCase(jenis)) {
//                return value;
//            }
//        }
//
//        throw new IllegalArgumentException("Jenis cucian tidak dikenali: " + jenis);
//    }
//
//    public static PriceOrderTipeCucian toPriceOrderTipeCucian(PesananTipeCucian tipe) {
//        if (tipe == null) return null;
//
//        for (PriceOrderTipeCucian value : PriceOrderTipeCucian.values()) {
//            if (value.getLiteral().equalsIgnoreCase(tipe.getLiteral())) {
//                return value;
//            }
//        }
//
//        throw new IllegalArgumentException("Tipe cucian (PriceOrder) tidak dikenali: " + tipe.getLiteral());
//    }
//
//    public static PriceOrderJenisCucian toPriceOrderJenisCucian(PesananJenisCucian jenis) {
//        if (jenis == null) return null;
//
//        for (PriceOrderJenisCucian value : PriceOrderJenisCucian.values()) {
//            if (value.getLiteral().equalsIgnoreCase(jenis.getLiteral())) {
//                return value;
//            }
//        }
//
//        throw new IllegalArgumentException("Jenis cucian (PriceOrder) tidak dikenali: " + jenis.getLiteral());
//    }
//}
