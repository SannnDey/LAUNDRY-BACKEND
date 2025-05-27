package com.haylaundry.service.backend.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class ApiKeyConfig {

    // Menyuntikkan API Key valid dari application.properties
    @ConfigProperty(name = "api.key.valid")
    private String apiKeyValid;

    // Getter method untuk mengakses API Key valid
    public String getValidApiKey() {
        return apiKeyValid;
    }
}
