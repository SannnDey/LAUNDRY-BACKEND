package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;

public class PriceOrder {
    public static double getHargaPerKg(PesananTipeCucian tipeCucian, PesananJenisCucian jenisCucian) {
        switch (tipeCucian) {
            case Super_Express_3_Jam_Komplit:
                switch (jenisCucian) {
                    case Komplit:
                        return 15000.0;
                    case Cuci_Lipat:
                        return 10000.0;
                    case Setrika:
                        return 8000.0;
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            case Express_1_Hari:
                switch (jenisCucian) {
                    case Komplit:
                        return 10000.0;
                    case Cuci_Lipat:
                        return 8000.0;
                    case Setrika:
                        return 7000.0;
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            case Standar_2_Hari:
                switch (jenisCucian) {
                    case Komplit:
                        return 8000.0;
                    case Cuci_Lipat:
                        return 6000.0;
                    case Setrika:
                        return 6000.0;
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            case Reguler_3_Hari:
                switch (jenisCucian) {
                    case Komplit:
                        return 7000.0;
                    case Cuci_Lipat:
                        return 5000.0;
                    case Setrika:
                        return 5000.0;
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            default:
                throw new IllegalArgumentException("Tipe cucian tidak dikenali");
        }
    }

    public static double hitungHargaTotal(PesananTipeCucian tipeCucian, PesananJenisCucian jenisCucian, double qty) {
        double hargaPerKg = getHargaPerKg(tipeCucian, jenisCucian);
        return hargaPerKg * qty;  // Mengalikan harga per kg dengan qty
    }
}
