package Identity.App.src.main.java.Identity.App.UseCases;

import Identity.App.src.main.java.Identity.App.Repositories.AuthTokenStore;
import Identity.App.src.main.java.Identity.App.Repositories.UserRepository;
import Identity.App.src.main.java.Identity.App.Services.AuthToken;
import Identity.App.src.main.java.Identity.App.Services.PasswordHasher;
import Identity.App.src.main.java.Identity.Domain.User;

public class LoginUserUseCase implements ILoginUserUseCase {

    private final UserRepository userRepository;
    private final AuthTokenStore tokenStore;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(UserRepository userRepository, AuthTokenStore tokenStore,
                            PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.tokenStore = tokenStore;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public AuthToken login(LoginUser command) throws Exception {
        if (command == null)
            throw new IllegalArgumentException("command must not be null");
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("invalid credentials"));
        String candidate = passwordHasher.hash(command.getPassword(), user.getPasswordSalt());
        if (!user.passwordMatches(candidate))
            throw new InvalidCredentialsException("invalid credentials");
        AuthToken token = new AuthToken(user.getId());
        tokenStore.save(token);
        return token;
    }

    @Override
    public void handle(LoginUser command) throws Exception {
        login(command);
    }
}
