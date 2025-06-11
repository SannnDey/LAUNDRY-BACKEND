package com.haylaundry.service.backend.core.utils;

import java.util.Random;
public class InvoiceGenerator {
    private static final String PREFIX = "INV-";
    private static final int NUMBER_LENGTH = 5;

    public static String generateNoFaktur() {
        Random random = new Random();
        int randomNumber = random.nextInt(99999);
        String formattedNumber = String.format("%05d", randomNumber);
        return PREFIX + formattedNumber;
    }

    public static void main(String[] args) {
        String noFaktur = generateNoFaktur();
        System.out.println("No Faktur yang dihasilkan: " + noFaktur);
    }
}
