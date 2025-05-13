package com.haylaundry.service.backend.core.utils;

import java.util.Random;
public class InvoiceGenerator {
    private static final String PREFIX = "INV-";
    private static final int NUMBER_LENGTH = 5; // Jumlah digit angka acak

    // Fungsi untuk menghasilkan no_faktur acak
    public static String generateNoFaktur() {
        Random random = new Random();
        int randomNumber = random.nextInt(99999); // Angka acak antara 0 dan 99999
        String formattedNumber = String.format("%05d", randomNumber); // Format angka menjadi 5 digit
        return PREFIX + formattedNumber;
    }

    public static void main(String[] args) {
        // Contoh penggunaan
        String noFaktur = generateNoFaktur();
        System.out.println("No Faktur yang dihasilkan: " + noFaktur);
    }
}
