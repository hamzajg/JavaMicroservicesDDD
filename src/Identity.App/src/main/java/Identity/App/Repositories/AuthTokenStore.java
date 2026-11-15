package Identity.App.src.main.java.Identity.App.Repositories;

import Identity.App.src.main.java.Identity.App.Services.AuthToken;
import java.util.Optional;
import java.util.UUID;

public interface AuthTokenStore {

    void save(AuthToken token);

    Optional<AuthToken> findByToken(String token);

    void remove(String token);
}
