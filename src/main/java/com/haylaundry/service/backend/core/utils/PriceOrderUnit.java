package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.jooq.gen.enums.ItemPesananSatuanKategoriBarang;
import com.haylaundry.service.backend.jooq.gen.enums.ItemPesananSatuanUkuran;
import com.haylaundry.service.backend.jooq.gen.enums.ItemPesananSatuanJenisLayanan;

public class PriceOrderUnit {

    public static double hitungHarga(String kategoriBarang, String ukuran, String jenisLayanan) {
        if (kategoriBarang == null || kategoriBarang.trim().isEmpty()) {
            throw new IllegalArgumentException("Kategori barang tidak boleh kosong atau null");
        }
        if (ukuran == null || ukuran.trim().isEmpty()) {
            throw new IllegalArgumentException("Ukuran tidak boleh kosong atau null");
        }
        if (jenisLayanan == null || jenisLayanan.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis layanan tidak boleh kosong atau null");
        }

        ItemPesananSatuanKategoriBarang kategoriEnum = tryLookupKategori(kategoriBarang);
        ItemPesananSatuanUkuran ukuranEnum = tryLookupUkuran(ukuran);
        ItemPesananSatuanJenisLayanan layananEnum = tryLookupJenisLayanan(jenisLayanan);

        return hitungHarga(kategoriEnum, ukuranEnum, layananEnum);
    }

    public static double hitungHarga(ItemPesananSatuanKategoriBarang kategoriBarang, ItemPesananSatuanUkuran ukuran, ItemPesananSatuanJenisLayanan jenisLayanan) {
        switch (kategoriBarang) {
            case Bed_Cover:
                return hitungHargaBedCover(ukuran, jenisLayanan);
            case Boneka:
                return hitungHargaBoneka(ukuran, jenisLayanan);
            case Bantal_Guling:
                return hitungHargaBantalGuling(ukuran, jenisLayanan);
            case Sepatu:
                return hitungHargaSepatu(ukuran, jenisLayanan); // Menambahkan case untuk Sepatu
            default:
                throw new IllegalArgumentException("Kategori barang tidak dikenali: " + kategoriBarang);
        }
    }

    private static ItemPesananSatuanKategoriBarang tryLookupKategori(String kategori) {
        for (ItemPesananSatuanKategoriBarang v : ItemPesananSatuanKategoriBarang.values()) {
            if (v.getLiteral().equalsIgnoreCase(kategori.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Kategori barang tidak dikenali: " + kategori);
    }

    private static ItemPesananSatuanUkuran tryLookupUkuran(String ukuran) {
        for (ItemPesananSatuanUkuran v : ItemPesananSatuanUkuran.values()) {
            if (v.getLiteral().equalsIgnoreCase(ukuran.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Ukuran tidak dikenali: " + ukuran);
    }

    private static ItemPesananSatuanJenisLayanan tryLookupJenisLayanan(String layanan) {
        for (ItemPesananSatuanJenisLayanan v : ItemPesananSatuanJenisLayanan.values()) {
            if (v.getLiteral().equalsIgnoreCase(layanan.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Jenis layanan tidak dikenali: " + layanan);
    }

    private static double hitungHargaBedCover(ItemPesananSatuanUkuran ukuran, ItemPesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan == ItemPesananSatuanJenisLayanan.Standar_3_Hari) {
            if (ukuran == ItemPesananSatuanUkuran.Kecil || ukuran == ItemPesananSatuanUkuran.Sedang) {
                return 15000;
            } else if (ukuran == ItemPesananSatuanUkuran.Besar) {
                return 20000;
            }
        } else if (jenisLayanan == ItemPesananSatuanJenisLayanan.Express_1_Hari) {
            if (ukuran == ItemPesananSatuanUkuran.Kecil || ukuran == ItemPesananSatuanUkuran.Sedang) {
                return 20000;
            } else if (ukuran == ItemPesananSatuanUkuran.Besar) {
                return 30000;
            }
        }
        throw new IllegalArgumentException("Ukuran atau jenis layanan tidak valid untuk Bed Cover");
    }

    private static double hitungHargaBoneka(ItemPesananSatuanUkuran ukuran, ItemPesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan != ItemPesananSatuanJenisLayanan.Standar_3_Hari) {
            throw new IllegalArgumentException("Boneka hanya tersedia untuk layanan standar");
        }
        switch (ukuran) {
            case Kecil:
                return 5000;
            case Sedang:
                return 15000;
            case Besar:
                return 35000;
            default:
                throw new IllegalArgumentException("Ukuran boneka tidak valid");
        }
    }

    private static double hitungHargaBantalGuling(ItemPesananSatuanUkuran ukuran, ItemPesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan != ItemPesananSatuanJenisLayanan.Standar_3_Hari) {
            throw new IllegalArgumentException("Bantal/Guling hanya tersedia untuk layanan standar");
        }
        switch (ukuran) {
            case Kecil:
                return 5000;
            case Besar:
                return 15000;
            default:
                throw new IllegalArgumentException("Ukuran bantal/guling tidak valid");
        }
    }

    // Tambahkan metode untuk menghitung harga Sepatu
    private static double hitungHargaSepatu(ItemPesananSatuanUkuran ukuran, ItemPesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan == ItemPesananSatuanJenisLayanan.Standar_4_Hari) {
            switch (ukuran) {
                case Kids_Shoes: // Untuk Sepatu Kecil (Kids Shoes)
                    return 20000;
                case Leather_Shoes: // Untuk Sepatu Sedang (Leather Shoes dan Sneakers Shoes)
                    return 35000;
                case Sneakers_Shoes:
                    return 35000;
                default:
                    throw new IllegalArgumentException("Ukuran sepatu tidak valid");
            }
        }
        throw new IllegalArgumentException("Sepatu hanya tersedia untuk layanan Standar 4 Hari");
    }
}
