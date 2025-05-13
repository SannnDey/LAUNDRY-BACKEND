package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.jooq.gen.enums.PesananJenisCucian;
import com.haylaundry.service.backend.jooq.gen.enums.PesananTipeCucian;

public class HargaCucian {
    // Method untuk mendapatkan harga per kg berdasarkan tipe dan jenis cucian
    public static double getHargaPerKg(PesananTipeCucian tipeCucian, PesananJenisCucian jenisCucian) {
        // Menentukan harga berdasarkan tipe cucian dan jenis cucian
        switch (tipeCucian) {
            case Super_Express_3_Jam_Komplit:
                switch (jenisCucian) {
                    case Komplit:
                        return 15000.0;  // Harga per 1kg untuk Super Express 3 Jam Komplit
                    case Cuci_Lipat:
                        return 10000.0;  // Harga per 1kg untuk Cuci Lipat
                    case Setrika:
                        return 8000.0;   // Harga per 1kg untuk Setrika
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            case Express_1_Hari:
                switch (jenisCucian) {
                    case Komplit:
                        return 10000.0;  // Harga per 1kg untuk Express 1 Hari Komplit
                    case Cuci_Lipat:
                        return 8000.0;   // Harga per 1kg untuk Cuci Lipat
                    case Setrika:
                        return 7000.0;   // Harga per 1kg untuk Setrika
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            case Standar_2_Hari:
                switch (jenisCucian) {
                    case Komplit:
                        return 8000.0;   // Harga per 1kg untuk Standar 2 Hari Komplit
                    case Cuci_Lipat:
                        return 6000.0;   // Harga per 1kg untuk Cuci Lipat
                    case Setrika:
                        return 6000.0;   // Harga per 1kg untuk Setrika
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            case Reguler_3_Hari:
                switch (jenisCucian) {
                    case Komplit:
                        return 7000.0;   // Harga per 1kg untuk Reguler 3 Hari Komplit
                    case Cuci_Lipat:
                        return 5000.0;   // Harga per 1kg untuk Cuci Lipat
                    case Setrika:
                        return 5000.0;   // Harga per 1kg untuk Setrika
                    default:
                        throw new IllegalArgumentException("Jenis cucian tidak dikenali");
                }

            default:
                throw new IllegalArgumentException("Tipe cucian tidak dikenali");
        }
    }

    // Method untuk menghitung harga total berdasarkan tipe cucian, jenis cucian dan kuantitas
    public static double hitungHargaTotal(PesananTipeCucian tipeCucian, PesananJenisCucian jenisCucian, double qty) {
        double hargaPerKg = getHargaPerKg(tipeCucian, jenisCucian);
        return hargaPerKg * qty;  // Mengalikan harga per kg dengan qty
    }
}
