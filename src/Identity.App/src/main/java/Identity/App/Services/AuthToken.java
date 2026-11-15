package Identity.App.src.main.java.Identity.App.Services;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthToken {

    private final String token;
    private final UUID userId;
    private final LocalDateTime issuedAt;

    public AuthToken(UUID userId) {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;
        this.issuedAt = LocalDateTime.now();
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
