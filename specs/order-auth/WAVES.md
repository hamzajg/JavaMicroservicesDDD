# Waves — order-auth

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- Valid token reaches order logic
- Missing token is rejected
- Invalid token is rejected
- Identity outage fails closed
- Introspection call is bounded

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As a shopper, I want my order calls to require a valid Identity token, so that only registered users can place, view, or cancel orders | M | — | BearerAuthFilter on /orders/* + TokenIntrospector calling Identity /identity/me; 401 invalid, 503 Identity down; introspection reuses timeout-configured RestTemplate. |
