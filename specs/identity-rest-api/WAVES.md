# Waves — identity-rest-api

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- Registration publishes event
- Password hashing

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As a developer, I want an Identity bounded context with credential storage and token issuing, so that callers can authenticate | L | — | Identity.App module — User aggregate (email, displayName, salted SHA-256 hash), UserRegistered, UserRepository + AuthTokenStore in-memory impls, PasswordHasher, RegisterUser/LoginUser/AuthenticateToken use cases; register module; Spock specs. |

## Wave 2 — iteration 2

Scenarios proven by this wave:
- Register user via API
- Login issues token
- Authenticated me endpoint

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US2 | As an API consumer, I want an Identity REST service on :8082, so that I can register, login and identify callers | M | US1 | Identity.Api module — POST /identity/register, POST /identity/login, GET /identity/me (Bearer); 201/200/400/401/409; DTOs; @Bean wiring; MockMvc tests; README/AGENTS/PRODUCT refresh. |
