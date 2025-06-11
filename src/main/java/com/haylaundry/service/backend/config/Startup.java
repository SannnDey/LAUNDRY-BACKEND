package com.haylaundry.service.backend.config;

import com.haylaundry.service.backend.core.utils.ApiKeyService;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class Startup {

    @Inject
    private ApiKeyService apiKeyService;

    public void init() {
        Set<String> initialApiKeys = Set.of("your-api-key-valid");
        apiKeyService.loadInitialApiKeys(initialApiKeys);
    }
}
