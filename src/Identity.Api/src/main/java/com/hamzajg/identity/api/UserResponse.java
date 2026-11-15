package com.hamzajg.identity.api;

import Identity.App.src.main.java.Identity.Domain.User;
import java.util.UUID;

public class UserResponse {

    private final UUID id;
    private final String email;
    private final String displayName;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
}
