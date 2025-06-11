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
    private final Set<String> validApiKeys = new HashSet<>();

    public void addApiKey(String apiKey) {
        validApiKeys.add(apiKey);
    }

    public boolean isValidApiKey(String apiKey) {
        return apiKey.equals(apiKeyConfig.getValidApiKey());
    }

    public void removeApiKey(String apiKey) {
        validApiKeys.remove(apiKey);
    }

    public void loadInitialApiKeys(Set<String> initialApiKeys) {
        validApiKeys.addAll(initialApiKeys);
    }
}
