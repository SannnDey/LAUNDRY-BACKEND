package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.jooq.gen.enums.PesananSatuanKategoriBarang;
import com.haylaundry.service.backend.jooq.gen.enums.PesananSatuanUkuran;
import com.haylaundry.service.backend.jooq.gen.enums.PesananSatuanJenisLayanan;

public class HargaCucianSatuan {

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

        PesananSatuanKategoriBarang kategoriEnum = tryLookupKategori(kategoriBarang);
        PesananSatuanUkuran ukuranEnum = tryLookupUkuran(ukuran);
        PesananSatuanJenisLayanan layananEnum = tryLookupJenisLayanan(jenisLayanan);

        return hitungHarga(kategoriEnum, ukuranEnum, layananEnum);
    }

    public static double hitungHarga(PesananSatuanKategoriBarang kategoriBarang, PesananSatuanUkuran ukuran, PesananSatuanJenisLayanan jenisLayanan) {
        switch (kategoriBarang) {
            case Bed_Cover:
                return hitungHargaBedCover(ukuran, jenisLayanan);
            case Boneka:
                return hitungHargaBoneka(ukuran, jenisLayanan);
            case Bantal_2fGuling:
                return hitungHargaBantalGuling(ukuran, jenisLayanan);
            default:
                throw new IllegalArgumentException("Kategori barang tidak dikenali: " + kategoriBarang);
        }
    }

    private static PesananSatuanKategoriBarang tryLookupKategori(String kategori) {
        for (PesananSatuanKategoriBarang v : PesananSatuanKategoriBarang.values()) {
            if (v.getLiteral().equalsIgnoreCase(kategori.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Kategori barang tidak dikenali: " + kategori);
    }

    private static PesananSatuanUkuran tryLookupUkuran(String ukuran) {
        for (PesananSatuanUkuran v : PesananSatuanUkuran.values()) {
            if (v.getLiteral().equalsIgnoreCase(ukuran.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Ukuran tidak dikenali: " + ukuran);
    }

    private static PesananSatuanJenisLayanan tryLookupJenisLayanan(String layanan) {
        for (PesananSatuanJenisLayanan v : PesananSatuanJenisLayanan.values()) {
            if (v.getLiteral().equalsIgnoreCase(layanan.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Jenis layanan tidak dikenali: " + layanan);
    }

    private static double hitungHargaBedCover(PesananSatuanUkuran ukuran, PesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan == PesananSatuanJenisLayanan.Standar_3_Hari) {
            if (ukuran == PesananSatuanUkuran.Kecil || ukuran == PesananSatuanUkuran.Sedang) {
                return 15000;
            } else if (ukuran == PesananSatuanUkuran.Besar) {
                return 20000;
            }
        } else if (jenisLayanan == PesananSatuanJenisLayanan.Express_1_Hari) {
            if (ukuran == PesananSatuanUkuran.Kecil || ukuran == PesananSatuanUkuran.Sedang) {
                return 20000;
            } else if (ukuran == PesananSatuanUkuran.Besar) {
                return 30000;
            }
        }
        throw new IllegalArgumentException("Ukuran atau jenis layanan tidak valid untuk Bed Cover");
    }

    private static double hitungHargaBoneka(PesananSatuanUkuran ukuran, PesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan != PesananSatuanJenisLayanan.Standar_3_Hari) {
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

    private static double hitungHargaBantalGuling(PesananSatuanUkuran ukuran, PesananSatuanJenisLayanan jenisLayanan) {
        if (jenisLayanan != PesananSatuanJenisLayanan.Standar_3_Hari) {
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

}
