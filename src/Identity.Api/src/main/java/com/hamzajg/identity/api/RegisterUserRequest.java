package com.hamzajg.identity.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterUserRequest {

    private final String email;
    private final String displayName;
    private final String password;

    @JsonCreator
    public RegisterUserRequest(@JsonProperty("email") String email,
                               @JsonProperty("displayName") String displayName,
                               @JsonProperty("password") String password) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPassword() { return password; }
}
