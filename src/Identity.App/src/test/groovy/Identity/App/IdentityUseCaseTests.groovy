package Identity.App;

import spock.lang.*
import Identity.App.src.main.java.Identity.App.UseCases.*
import Identity.App.src.main.java.Identity.App.Repositories.*
import Identity.App.src.main.java.Identity.App.Services.*
import Identity.App.src.main.java.Identity.Domain.*
import Identity.App.src.main.java.Identity.Events.*
import Services.Common.src.main.java.Services.Common.Services.*

public class IdentityUseCaseTests extends Specification {

    IPublisherServices pubServices
    UserRepository userRepository
    AuthTokenStore tokenStore
    PasswordHasher passwordHasher
    RegisterUserUseCase registerUseCase
    LoginUserUseCase loginUseCase
    AuthenticateTokenUseCase authenticateUseCase

    def setup() {
        pubServices = new InMemoryPublisherServices()
        userRepository = new InMemoryUserRepository()
        tokenStore = new InMemoryAuthTokenStore()
        passwordHasher = new PasswordHasher()
        registerUseCase = new RegisterUserUseCase(pubServices, userRepository, passwordHasher)
        loginUseCase = new LoginUserUseCase(userRepository, tokenStore, passwordHasher)
        authenticateUseCase = new AuthenticateTokenUseCase(tokenStore, userRepository)
    }

    def "Should register a user and publish UserRegistered"() {
        when:
        def user = registerUseCase.register(new RegisterUser("jane@example.com", "Jane", "password123"))
        then:
        // covers scenario: Registration publishes event
        user.id != null
        def events = ((InMemoryPublisherServices) pubServices).getEvents()
        events.size() == 1
        events[0] instanceof UserRegistered
    }

    def "Should store a salted hash, never the plaintext password"() {
        when:
        def user = registerUseCase.register(new RegisterUser("jane@example.com", "Jane", "password123"))
        then:
        // covers scenario: Password hashing
        !user.passwordHash.contains("password123")
        user.passwordSalt != null
        userRepository.findByEmail("jane@example.com").get().passwordHash == user.passwordHash
    }

    def "Login succeeds with plaintext and fails with the stored hash"() {
        given:
        def user = registerUseCase.register(new RegisterUser("jane@example.com", "Jane", "password123"))

        when:
        def token = loginUseCase.login(new LoginUser("jane@example.com", "password123"))
        then:
        token.token != null
        token.userId == user.id

        when:
        loginUseCase.login(new LoginUser("jane@example.com", user.passwordHash))
        then:
        thrown(InvalidCredentialsException)
    }

    def "Login rejects wrong password and unknown email"() {
        given:
        registerUseCase.register(new RegisterUser("jane@example.com", "Jane", "password123"))

        when:
        loginUseCase.login(new LoginUser("jane@example.com", "wrong-password"))
        then:
        thrown(InvalidCredentialsException)

        when:
        loginUseCase.login(new LoginUser("nobody@example.com", "password123"))
        then:
        thrown(InvalidCredentialsException)
    }

    def "Authenticate token resolves the user; unknown token does not"() {
        given:
        def user = registerUseCase.register(new RegisterUser("jane@example.com", "Jane", "password123"))
        def token = loginUseCase.login(new LoginUser("jane@example.com", "password123"))

        expect:
        // covers scenario: Authenticated me endpoint
        authenticateUseCase.authenticate(token.token).isPresent()
        authenticateUseCase.authenticate(token.token).get().id == user.id
        !authenticateUseCase.authenticate("bogus-token").isPresent()
        !authenticateUseCase.authenticate(null).isPresent()
    }

    def "Rejects duplicate email, weak password and bad email"() {
        given:
        registerUseCase.register(new RegisterUser("jane@example.com", "Jane", "password123"))

        when:
        registerUseCase.register(new RegisterUser("JANE@example.com", "Jane2", "password123"))
        then:
        thrown(DuplicateEmailException)

        when:
        registerUseCase.register(new RegisterUser("new@example.com", "New", "short"))
        then:
        thrown(IllegalArgumentException)

        when:
        registerUseCase.register(new RegisterUser("not-an-email", "New", "password123"))
        then:
        thrown(IllegalArgumentException)
    }
}
