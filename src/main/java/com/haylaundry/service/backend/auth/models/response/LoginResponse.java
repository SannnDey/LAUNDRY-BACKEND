package com.haylaundry.service.backend.auth.models.response;

public class LoginResponse {

    private UserInfoResponse user;
    private String token;

    public LoginResponse(UserInfoResponse user, String token) {
        this.user = user;
        this.token = token;
    }

    // Getter dan Setter
    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
