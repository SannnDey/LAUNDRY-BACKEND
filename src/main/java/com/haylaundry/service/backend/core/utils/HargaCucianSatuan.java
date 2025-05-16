package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanKategoriBarang;
import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanUkuran;
import com.haylaundry.service.backend.jooq.gen.enums.DetailPesananSatuanJenisLayanan;

public class HargaCucianSatuan {
        public static double hitungHarga(String kategoriBarang, String ukuran, String jenisLayanan) {
            switch (kategoriBarang.toLowerCase()) {
                case "bed_cover":
                    return hitungHargaBedCover(ukuran, jenisLayanan);
                case "boneka":
                    return hitungHargaBoneka(ukuran, jenisLayanan);
                case "bantal_guling":
                    return hitungHargaBantalGuling(ukuran, jenisLayanan);
                default:
                    throw new IllegalArgumentException("Kategori barang tidak dikenali: " + kategoriBarang);
            }
        }

        private static double hitungHargaBedCover(String ukuran, String jenisLayanan) {
            if (jenisLayanan.equalsIgnoreCase("standar")) {
                if (ukuran.equalsIgnoreCase("kecil") || ukuran.equalsIgnoreCase("sedang")) {
                    return 15000;
                } else if (ukuran.equalsIgnoreCase("besar")) {
                    return 20000;
                }
            } else if (jenisLayanan.equalsIgnoreCase("express")) {
                if (ukuran.equalsIgnoreCase("kecil") || ukuran.equalsIgnoreCase("sedang")) {
                    return 20000;
                } else if (ukuran.equalsIgnoreCase("besar")) {
                    return 30000;
                }
            }

            throw new IllegalArgumentException("Ukuran atau jenis layanan tidak valid untuk Bed Cover");
        }

        private static double hitungHargaBoneka(String ukuran, String jenisLayanan) {
            if (!jenisLayanan.equalsIgnoreCase("standar")) {
                throw new IllegalArgumentException("Boneka hanya tersedia untuk layanan standar");
            }

            switch (ukuran.toLowerCase()) {
                case "kecil":
                    return 5000;
                case "sedang":
                    return 15000;
                case "besar":
                    return 35000;
                default:
                    throw new IllegalArgumentException("Ukuran boneka tidak valid");
            }
        }

        private static double hitungHargaBantalGuling(String ukuran, String jenisLayanan) {
            if (!jenisLayanan.equalsIgnoreCase("standar")) {
                throw new IllegalArgumentException("Bantal/Guling hanya tersedia untuk layanan standar");
            }

            switch (ukuran.toLowerCase()) {
                case "kecil":
                    return 5000;
                case "besar":
                    return 15000;
                default:
                    throw new IllegalArgumentException("Ukuran bantal/guling tidak valid");
            }
        }
    }
