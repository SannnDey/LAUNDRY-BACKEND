package com.haylaundry.service.backend.core.utils;

import com.haylaundry.service.backend.config.ApiKeyConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class ApiKeyService {

    @Inject
    private ApiKeyConfig apiKeyConfig;

    // Set untuk menyimpan API Key yang valid
    private final Set<String> validApiKeys = new HashSet<>();

    // Menambahkan API Key yang valid (misalnya, dari database atau file konfigurasi)
    public void addApiKey(String apiKey) {
        validApiKeys.add(apiKey);
    }

    // Mengecek apakah API Key valid
    // Fungsi untuk memverifikasi apakah API Key valid
    public boolean isValidApiKey(String apiKey) {
        return apiKey.equals(apiKeyConfig.getValidApiKey());
    }

    // Menghapus API Key (jika diperlukan)
    public void removeApiKey(String apiKey) {
        validApiKeys.remove(apiKey);
    }

    // Fungsi untuk memuat API Key awal (misalnya, dari konfigurasi atau database)
    public void loadInitialApiKeys(Set<String> initialApiKeys) {
        validApiKeys.addAll(initialApiKeys);
    }
}
