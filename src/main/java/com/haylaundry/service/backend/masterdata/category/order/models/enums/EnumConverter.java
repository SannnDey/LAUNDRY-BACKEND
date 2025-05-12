package com.haylaundry.service.backend.masterdata.category.order.models.enums;

import com.haylaundry.service.backend.jooq.gen.enums.*;

public class EnumConverter {

    // Konversi TipeCucian dari PesananRecord ke OrderRequest
    public static TipeCucian convertPesananTipeCucianToTipeCucian(com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian pesananTipeCucian) {
        switch (pesananTipeCucian) {
            case Super_Express_3_Jam_Komplit:
                return TipeCucian.SUPER_EXPRESS_3_JAM_KOMPLIT;
            case Express_1_Hari:
                return TipeCucian.EXPRESS_1_HARI;
            case Standar_2_Hari:
                return TipeCucian.STANDAR_2_HARI;
            case Reguler_3_Hari:
                return TipeCucian.REGULER_3_HARI;
            case Laundry_Satuan:
                return TipeCucian.LAUNDRY_SATUAN;
            default:
                throw new IllegalArgumentException("Unknown TipeCucian: " + pesananTipeCucian);
        }
    }

    // Konversi TipeCucian dari OrderRequest ke PesananRecord
    public static com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian convertTipeCucianToPesananTipeCucian(TipeCucian tipeCucian) {
        switch (tipeCucian) {
            case SUPER_EXPRESS_3_JAM_KOMPLIT:
                return PesananTipeCucian.Super_Express_3_Jam_Komplit;
            case EXPRESS_1_HARI:
                return PesananTipeCucian.Express_1_Hari;
            case STANDAR_2_HARI:
                return PesananTipeCucian.Standar_2_Hari;
            case REGULER_3_HARI:
                return PesananTipeCucian.Reguler_3_Hari;
            case LAUNDRY_SATUAN:
                return PesananTipeCucian.Laundry_Satuan;
            default:
                throw new IllegalArgumentException("Unknown TipeCucian: " + tipeCucian);
        }
    }

    // Konversi JenisCucian dari PesananRecord ke OrderRequest
    public static JenisCucian convertPesananJenisCucianToJenisCucian(com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian pesananJenisCucian) {
        switch (pesananJenisCucian) {
            case Komplit:
                return JenisCucian.KOMPLIT;
            case Cuci_Lipat:
                return JenisCucian.CUCI_LIPAT;
            case Setrika:
                return JenisCucian.SETRIKA;
            default:
                throw new IllegalArgumentException("Unknown JenisCucian: " + pesananJenisCucian);
        }
    }

    // Konversi JenisCucian dari OrderRequest ke PesananRecord
    public static com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian convertJenisCucianToPesananJenisCucian(JenisCucian jenisCucian) {
        switch (jenisCucian) {
            case KOMPLIT:
                return PesananJenisCucian.Komplit;
            case CUCI_LIPAT:
                return PesananJenisCucian.Cuci_Lipat;
            case SETRIKA:
                return PesananJenisCucian.Setrika;
            default:
                throw new IllegalArgumentException("Unknown JenisCucian: " + jenisCucian);
        }
    }

    // Konversi StatusBayar dari PesananRecord ke OrderRequest
    public static StatusBayar convertPesananStatusBayarToStatusBayar(com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar pesananStatusBayar) {
        switch (pesananStatusBayar) {
            case Belum_Lunas:
                return StatusBayar.BELUM_LUNAS;
            case Lunas:
                return StatusBayar.LUNAS;
            default:
                throw new IllegalArgumentException("Unknown StatusBayar: " + pesananStatusBayar);
        }
    }

    // Konversi StatusBayar dari OrderRequest ke PesananRecord
    public static com.haylaundry.service.backend.jooq.gen.enums.PesananStatusBayar convertStatusBayarToPesananStatusBayar(StatusBayar statusBayar) {
        switch (statusBayar) {
            case BELUM_LUNAS:
                return PesananStatusBayar.Belum_Lunas;
            case LUNAS:
                return PesananStatusBayar.Lunas;
            default:
                throw new IllegalArgumentException("Unknown StatusBayar: " + statusBayar);
        }
    }

    // Konversi StatusOrder dari PesananRecord ke OrderRequest
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

    // Konversi StatusOrder dari OrderRequest ke PesananRecord
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

    // Konversi TipePembayaran dari PesananRecord ke OrderRequest
    public static TipePembayaran convertPesananTipePembayaranToTipePembayaran(com.haylaundry.service.backend.jooq.gen.enums.PesananTipePembayaran pesananTipePembayaran) {
        switch (pesananTipePembayaran) {
            case Cash:
                return TipePembayaran.CASH;
            case QRIS:
                return TipePembayaran.QRIS;
            default:
                throw new IllegalArgumentException("Unknown TipePembayaran: " + pesananTipePembayaran);
        }
    }

    // Konversi TipePembayaran dari OrderRequest ke PesananRecord
    public static com.haylaundry.service.backend.jooq.gen.enums.PesananTipePembayaran convertTipePembayaranToPesananTipePembayaran(TipePembayaran tipePembayaran) {
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
