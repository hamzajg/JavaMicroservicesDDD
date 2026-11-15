package Identity.App.src.main.java.Identity.App.UseCases;

import Identity.App.src.main.java.Identity.App.Repositories.AuthTokenStore;
import Identity.App.src.main.java.Identity.App.Repositories.UserRepository;
import Identity.App.src.main.java.Identity.Domain.User;
import java.util.Optional;

public class AuthenticateTokenUseCase {

    private final AuthTokenStore tokenStore;
    private final UserRepository userRepository;

    public AuthenticateTokenUseCase(AuthTokenStore tokenStore, UserRepository userRepository) {
        this.tokenStore = tokenStore;
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String token) {
        if (token == null || token.isBlank())
            return Optional.empty();
        return tokenStore.findByToken(token)
                .flatMap(t -> userRepository.findById(t.getUserId()));
    }
}
