package com.hamzajg.identity.api;

import Identity.App.src.main.java.Identity.App.Services.AuthToken;

public class LoginResponse {

    private final String token;
    private final String userId;

    public LoginResponse(AuthToken authToken) {
        this.token = authToken.getToken();
        this.userId = authToken.getUserId().toString();
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
}
