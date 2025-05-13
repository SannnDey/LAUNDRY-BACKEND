package com.haylaundry.service.backend.modules.auth.controller;

import com.haylaundry.service.backend.modules.auth.models.response.LoginResponse;
import com.haylaundry.service.backend.modules.auth.models.request.UserAuthRequest;
import com.haylaundry.service.backend.modules.auth.models.response.UserInfoResponse;
import com.haylaundry.service.backend.modules.auth.service.UserAuthService;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.time.Instant;

@Path("/api/auth")
public class UserAuthController {

    @Inject
    private UserAuthService userAuthService;

    @GET
    @RolesAllowed("admin")
    public Response getData() {
        return Response.ok(userAuthService.getAllUsers()).build();
    }

    @POST
    @Path("/register")
    public Response register(UserAuthRequest request) {
        try {
            if (!request.getRole().equalsIgnoreCase("admin") && !request.getRole().equalsIgnoreCase("karyawan")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Role harus admin atau karyawan").build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity(userAuthService.create(request))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(UserAuthRequest request) {
        try {
            var user = userAuthService.login(request.getUsername(), request.getPassword());

            String jwtToken = Jwt.issuer("user-service")
                    .upn(user.getUsername())
                    .groups(user.getRole().toLowerCase()) // ex: "admin" atau "karyawan"
                    .audience("user-service")
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .sign();

            var responseBody = new LoginResponse(new UserInfoResponse(user.getUsername(), user.getRole()), jwtToken);
            return Response.ok(responseBody).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }


    @PUT
    @RolesAllowed("admin")
    @Path("/update/{userId}")
    public Response updateUser(@PathParam("userId") String userId, UserAuthRequest request) {
        try {
            var updatedUser = userAuthService.update(userId, request);
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @DELETE
    @Path("/delete/{userId}")
    @RolesAllowed("admin")
    public Response deleteUser(@PathParam("userId") String userId) {
        try {
            userAuthService.delete(userId);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
