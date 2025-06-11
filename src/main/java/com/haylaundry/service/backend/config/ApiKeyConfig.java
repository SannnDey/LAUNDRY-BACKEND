package com.haylaundry.service.backend.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class ApiKeyConfig {
    @ConfigProperty(name = "api.key.valid")
    private String apiKeyValid;
    public String getValidApiKey() {
        return apiKeyValid;
    }
}
