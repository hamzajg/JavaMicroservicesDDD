# Spec: identity-rest-api

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

The repo now has Customer (:8080) and Product (:8081) services but no
Identity context — `specs/PRODUCT.md` lists `identity-rest-api` as the
candidate that completes the classic e-shop trio. Without it there is no
way to authenticate callers, and neither existing service can tell who
is calling.

## Users

- API consumers that need to register accounts and obtain credentials.
- Future protected endpoints (orders, account data) that will require a
  bearer token issued by this service.

## Scope (in)

- New `Identity.App` module (`src/Identity.App`): `User` aggregate
  (UUID id, email, display name, salted password hash), `UserRegistered`
  event, `UserRepository` + in-memory impl, `AuthTokenStore` + in-memory
  impl.
- `PasswordHasher` (plain Java, `SecureRandom` salt + SHA-256 — no new
  deps; documented as kata-grade, not production crypto).
- Use cases: `RegisterUser` (email unique → 409, password rules),
  `LoginUser` (verify → issue token), `AuthenticateToken` (Bearer →
  user).
- New `Identity.Api` module (`src/Identity.Api`, `:8082`):
  `POST /identity/register`, `POST /identity/login`,
  `GET /identity/me` (Bearer) — 201/200/400/401/409 semantics,
  `@Bean` wiring, MockMvc tests.
- Contexts stay decoupled: Identity does not call Customer/Product.

## Scope (out)

- JWT/signing, refresh tokens, password reset, roles/authorization,
  persistence, HTTPS. Token = opaque random UUID (in-memory store).
- Wiring auth into Customer/Product endpoints (separate feature).

## Acceptance criteria

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | POST /identity/register returns 201 + user id; duplicate email → 409; weak input → 400 | Register user via API |
| 2 | POST /identity/login with correct credentials returns a token; wrong password → 401 | Login issues token |
| 3 | GET /identity/me with the token returns the user; without/with bad token → 401 | Authenticated me endpoint |
| 4 | Registration publishes a UserRegistered domain event | Registration publishes event |
| 5 | Passwords are never stored or returned in plaintext | Password hashing |

## Decisions

- Third service on **:8082** — completes Customer/Product/Identity trio,
  all runnable simultaneously.
- Salted SHA-256 via `java.security` — zero dependencies, honest kata
  crypto; README will say it is not production-grade (BCrypt/Argon2 is
  the real-world answer).
- Opaque UUID tokens in an in-memory store — no JWT infrastructure for
  an MVP; `AuthenticateToken` is the seam a real IdP would replace.
