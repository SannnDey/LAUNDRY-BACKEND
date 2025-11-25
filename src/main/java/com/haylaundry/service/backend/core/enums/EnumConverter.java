package com.haylaundry.service.backend.core.enums;

import com.haylaundry.service.backend.jooq.gen.enums.*;

public class EnumConverter {
    public static PaymentStatus convertPesananStatusBayarToStatusBayar(com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar pesananStatusBayar) {
        switch (pesananStatusBayar) {
            case Belum_Lunas:
                return PaymentStatus.BELUM_LUNAS;
            case Lunas:
                return PaymentStatus.LUNAS;
            default:
                throw new IllegalArgumentException("Unknown StatusBayar: " + pesananStatusBayar);
        }
    }

    public static com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar convertStatusBayarToPesananStatusBayar(PaymentStatus statusBayar) {
        switch (statusBayar) {
            case BELUM_LUNAS:
                return PesananStatusBayar.Belum_Lunas;
            case LUNAS:
                return PesananStatusBayar.Lunas;
            default:
                throw new IllegalArgumentException("Unknown StatusBayar: " + statusBayar);
        }
    }

    public static StatusOrder convertPesananStatusOrderToStatusOrder(com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder pesananStatusOrder) {
        switch (pesananStatusOrder) {
            case Pickup:
                return StatusOrder.PICKUP;
            case Cuci:
                return StatusOrder.CUCI;
            case Selesai:
                return StatusOrder.SELESAI;
            default:
                throw new IllegalArgumentException("Unknown StatusOrder: " + pesananStatusOrder);
        }
    }

    public static com.haylaundry.service.backend.jooq.gen.enums.PesananStatusOrder convertStatusOrderToPesananStatusOrder(StatusOrder statusOrder) {
        switch (statusOrder) {
            case PICKUP:
                return PesananStatusOrder.Pickup;
            case CUCI:
                return PesananStatusOrder.Cuci;
            case SELESAI:
                return PesananStatusOrder.Selesai;
            default:
                throw new IllegalArgumentException("Unknown StatusOrder: " + statusOrder);
        }
    }

    public static PaymentType convertPesananTipePembayaranToTipePembayaran(com.haylaundry.service.backend.jooq.gen.enums.PesananTipePembayaran pesananTipePembayaran) {
        switch (pesananTipePembayaran) {
            case Cash:
                return PaymentType.CASH;
            case QRIS:
                return PaymentType.QRIS;
            default:
                throw new IllegalArgumentException("Unknown TipePembayaran: " + pesananTipePembayaran);
        }
    }

    public static com.haylaundry.service.backend.jooq.gen.enums.PesananTipePembayaran convertTipePembayaranToPesananTipePembayaran(PaymentType tipePembayaran) {
        switch (tipePembayaran) {
            case CASH:
                return PesananTipePembayaran.Cash;
            case QRIS:
                return com.haylaundry.service.backend.jooq.gen.enums.PesananTipePembayaran.QRIS;
            default:
                throw new IllegalArgumentException("Unknown TipePembayaran: " + tipePembayaran);
        }
    }
}
