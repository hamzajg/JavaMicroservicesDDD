package Identity.App.src.main.java.Identity.App.Repositories;

import Identity.App.src.main.java.Identity.App.Services.AuthToken;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAuthTokenStore implements AuthTokenStore {

    private final Map<String, AuthToken> tokens = new HashMap<>();

    public void save(AuthToken token) {
        tokens.put(token.getToken(), token);
    }

    public Optional<AuthToken> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }

    public void remove(String token) {
        tokens.remove(token);
    }
}
