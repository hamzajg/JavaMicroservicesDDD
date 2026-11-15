package com.hamzajg.identity.api;

import Identity.App.src.main.java.Identity.App.Repositories.AuthTokenStore;
import Identity.App.src.main.java.Identity.App.Repositories.InMemoryAuthTokenStore;
import Identity.App.src.main.java.Identity.App.Repositories.InMemoryUserRepository;
import Identity.App.src.main.java.Identity.App.Repositories.UserRepository;
import Identity.App.src.main.java.Identity.App.Services.PasswordHasher;
import Identity.App.src.main.java.Identity.App.UseCases.AuthenticateTokenUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.IRegisterUserUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.ILoginUserUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.LoginUserUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.RegisterUserUseCase;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import Services.Common.src.main.java.Services.Common.Services.InMemoryPublisherServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityConfig {

    @Bean
    public IPublisherServices publisherServices() {
        return new InMemoryPublisherServices();
    }

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public AuthTokenStore authTokenStore() {
        return new InMemoryAuthTokenStore();
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new PasswordHasher();
    }

    @Bean
    public IRegisterUserUseCase registerUserUseCase(IPublisherServices publisherServices,
                                                   UserRepository userRepository,
                                                   PasswordHasher passwordHasher) {
        return new RegisterUserUseCase(publisherServices, userRepository, passwordHasher);
    }

    @Bean
    public ILoginUserUseCase loginUserUseCase(UserRepository userRepository,
                                              AuthTokenStore authTokenStore,
                                              PasswordHasher passwordHasher) {
        return new LoginUserUseCase(userRepository, authTokenStore, passwordHasher);
    }

    @Bean
    public AuthenticateTokenUseCase authenticateTokenUseCase(AuthTokenStore authTokenStore,
                                                             UserRepository userRepository) {
        return new AuthenticateTokenUseCase(authTokenStore, userRepository);
    }
}
