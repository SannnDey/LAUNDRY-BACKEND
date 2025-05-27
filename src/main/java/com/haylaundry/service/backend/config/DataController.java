package com.haylaundry.service.backend.config;

import com.haylaundry.service.backend.core.utils.ApiKeyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/api/data")
public class DataController {

    @Inject
    private ApiKeyService apiKeyService;

    @GET
    @Path("/access")
    public Response accessData(@HeaderParam("API-Key") String apiKey) {
        // Memverifikasi apakah API Key valid
        if (!apiKeyService.isValidApiKey(apiKey)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid API Key").build();
        }

        // Jika API Key valid, kembalikan data
        return Response.ok("Data successfully retrieved").build();
    }
}
