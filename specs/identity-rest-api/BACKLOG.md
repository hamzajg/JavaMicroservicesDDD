# Backlog: identity-rest-api

Items are USER STORIES in the parseable format below — keep the heading
shape `## US<n> — <story>` and the field lines intact so `wave_plan` can
parse them. Write the story as: "As a <role>, I want <goal>, so that
<value>" — the title after the dash IS the story.

```
## US<n> — As a <role>, I want <goal>, so that <value>
- Scenarios: @w1, @w2
- Depends: US1, US3        (optional; omit for wave-1 items)
- Size: S|M|L
- Notes: <one line>
```

Order stories by user value; dependencies express technical order only.

## US1 — As a developer, I want an Identity bounded context with credential storage and token issuing, so that callers can authenticate
- Scenarios: @w1
- Size: L
- Notes: Identity.App module — User aggregate (email, displayName, salted SHA-256 hash), UserRegistered, UserRepository + AuthTokenStore in-memory impls, PasswordHasher, RegisterUser/LoginUser/AuthenticateToken use cases; register module; Spock specs.

## US2 — As an API consumer, I want an Identity REST service on :8082, so that I can register, login and identify callers
- Scenarios: @w2
- Depends: US1
- Size: M
- Notes: Identity.Api module — POST /identity/register, POST /identity/login, GET /identity/me (Bearer); 201/200/400/401/409; DTOs; @Bean wiring; MockMvc tests; README/AGENTS/PRODUCT refresh.
