package com.hamzajg.identity.api;

import Identity.App.src.main.java.Identity.App.UseCases.AuthenticateTokenUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.DuplicateEmailException;
import Identity.App.src.main.java.Identity.App.UseCases.IRegisterUserUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.ILoginUserUseCase;
import Identity.App.src.main.java.Identity.App.UseCases.InvalidCredentialsException;
import Identity.App.src.main.java.Identity.App.UseCases.LoginUser;
import Identity.App.src.main.java.Identity.App.UseCases.RegisterUser;
import Identity.App.src.main.java.Identity.Domain.User;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    private final IRegisterUserUseCase registerUserUseCase;
    private final ILoginUserUseCase loginUserUseCase;
    private final AuthenticateTokenUseCase authenticateTokenUseCase;

    public IdentityController(IRegisterUserUseCase registerUserUseCase,
                              ILoginUserUseCase loginUserUseCase,
                              AuthenticateTokenUseCase authenticateTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.authenticateTokenUseCase = authenticateTokenUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        try {
            User user = registerUserUseCase.register(new RegisterUser(
                    request.getEmail(), request.getDisplayName(), request.getPassword()));
            return ResponseEntity.created(URI.create("/identity/users/" + user.getId()))
                    .body(new UserResponse(user));
        } catch (DuplicateEmailException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            return new LoginResponse(loginUserUseCase.login(
                    new LoginUser(request.getEmail(), request.getPassword())));
        } catch (InvalidCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/me")
    public UserResponse me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = bearerToken(authorization);
        return authenticateTokenUseCase.authenticate(token)
                .map(UserResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid or missing token"));
    }

    private String bearerToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer "))
            return authorization.substring("Bearer ".length());
        return null;
    }
}
