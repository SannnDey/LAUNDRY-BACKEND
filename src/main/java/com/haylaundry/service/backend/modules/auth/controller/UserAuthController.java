package com.haylaundry.service.backend.modules.auth.controller;

import com.haylaundry.service.backend.modules.auth.models.response.LoginResponse;
import com.haylaundry.service.backend.modules.auth.models.request.UserAuthRequest;
import com.haylaundry.service.backend.modules.auth.models.response.UserInfoResponse;
import com.haylaundry.service.backend.modules.auth.service.UserAuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/auth")
public class UserAuthController {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthController.class);

    @Inject
    private UserAuthService userAuthService;

    @GET
    @RolesAllowed("admin")
    public Response getData() {
        logger.info("Mengambil data semua pengguna");
        return Response.ok(userAuthService.getAllUsers()).build();
    }

    @POST
    @Path("/register")
    public Response register(UserAuthRequest request) {
        try {
            if (!request.getRole().equalsIgnoreCase("admin") &&
                    !request.getRole().equalsIgnoreCase("karyawan") &&
                    !request.getRole().equalsIgnoreCase("pemilik")) {
                logger.warn("Role yang dipilih tidak valid: {}", request.getRole());
                return Response.status(Response.Status.BAD_REQUEST).entity("Role harus admin, karyawan, atau pemilik").build();
            }

            var user = userAuthService.create(request);
            logger.info("Pengguna berhasil dibuat dengan username: {}", request.getUsername());
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error saat registrasi pengguna: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @POST
    @Path("/login")
    public Response login(UserAuthRequest request) {
        try {
            var user = userAuthService.login(request.getUsername(), request.getPassword());

            String jwtToken = userAuthService.generateJwtToken(user);
            var responseBody = new LoginResponse(new UserInfoResponse(user.getUsername(), user.getRole()), jwtToken);

            logger.info("Login berhasil untuk pengguna: {}", request.getUsername());
            return Response.ok(responseBody).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error saat login: {}", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @PUT
    @RolesAllowed("admin")
    @Path("/update/{userId}")
    public Response updateUser(@PathParam("userId") String userId, UserAuthRequest request) {
        try {
            var updatedUser = userAuthService.update(userId, request);
            logger.info("Data pengguna dengan ID {} berhasil diperbarui", userId);
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error saat memperbarui data pengguna: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/delete/{userId}")
    @RolesAllowed("admin")
    public Response deleteUser(@PathParam("userId") String userId) {
        try {
            userAuthService.delete(userId);
            logger.info("Pengguna dengan ID {} berhasil dihapus", userId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error saat menghapus pengguna: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}