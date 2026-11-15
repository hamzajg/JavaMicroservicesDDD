# Validation — order-auth

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | Requests to /orders without a Bearer token are rejected 401 | Identity outage fails closed, Introspection call is bounded, Invalid token is rejected, Missing token is rejected, Valid token reaches order logic | 1 | IdentityTokenIntrospectorTest.java, OrderControllerTest.java |
| 2 | Requests with an invalid/expired token are rejected 401 | Identity outage fails closed, Introspection call is bounded, Invalid token is rejected, Missing token is rejected, Valid token reaches order logic | 1 | IdentityTokenIntrospectorTest.java, OrderControllerTest.java |
| 3 | A valid Identity token lets the request through to order… | Identity outage fails closed, Introspection call is bounded, Invalid token is rejected, Missing token is rejected, Valid token reaches order logic | 1 | IdentityTokenIntrospectorTest.java, OrderControllerTest.java |
| 4 | Identity being unreachable returns 503, not a hang or 500 | Identity outage fails closed, Introspection call is bounded, Invalid token is rejected, Missing token is rejected, Valid token reaches order logic | 1 | IdentityTokenIntrospectorTest.java, OrderControllerTest.java |
| 5 | The introspection call itself is bounded by timeouts | Identity outage fails closed, Introspection call is bounded, Invalid token is rejected, Missing token is rejected, Valid token reaches order logic | 1 | IdentityTokenIntrospectorTest.java, OrderControllerTest.java |

## Scenario coverage

- Scenarios: 5 total, 5 with detected test evidence, 0 without
- ✓ `Valid token reaches order logic` (@w1)
- ✓ `Missing token is rejected` (@w1)
- ✓ `Invalid token is rejected` (@w1)
- ✓ `Identity outage fails closed` (@w1)
- ✓ `Introspection call is bounded` (@w1)

## Demo script (manual walkthrough)

1. **Valid token reaches order logic** — run the app, perform the scenario, observe the outcome.

## Gaps

- none — spec is fully traced

Note: 'no evidence' means neither the scenario name nor a
token match was found in test files — verify by hand before
treating it as a real gap. '~' marks weak token-match evidence.