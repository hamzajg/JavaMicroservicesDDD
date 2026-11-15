package Identity.App.src.main.java.Identity.App.UseCases;

import Identity.App.src.main.java.Identity.App.Repositories.UserRepository;
import Identity.App.src.main.java.Identity.App.Services.PasswordHasher;
import Identity.App.src.main.java.Identity.Domain.User;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;

public class RegisterUserUseCase implements IRegisterUserUseCase {

    private final IPublisherServices pubServices;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(IPublisherServices pubServices, UserRepository userRepository,
                               PasswordHasher passwordHasher) {
        this.pubServices = pubServices;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User register(RegisterUser command) throws Exception {
        validate(command);
        if (userRepository.findByEmail(command.getEmail()).isPresent())
            throw new DuplicateEmailException("email already registered: " + command.getEmail());
        String salt = passwordHasher.generateSalt();
        User user = new User(command.getEmail(), command.getDisplayName(),
                passwordHasher.hash(command.getPassword(), salt), salt);
        userRepository.save(user);
        user.getDomainEvents().forEach(pubServices::publish);
        return user;
    }

    @Override
    public void handle(RegisterUser command) throws Exception {
        register(command);
    }

    private void validate(RegisterUser command) {
        if (command == null)
            throw new IllegalArgumentException("command must not be null");
        if (command.getEmail() == null || !command.getEmail().contains("@"))
            throw new IllegalArgumentException("a valid email is required");
        if (isBlank(command.getDisplayName()))
            throw new IllegalArgumentException("display name is required");
        if (command.getPassword() == null || command.getPassword().length() < 8)
            throw new IllegalArgumentException("password must be at least 8 characters");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
