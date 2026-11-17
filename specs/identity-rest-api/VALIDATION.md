# Validation — identity-rest-api

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | POST /identity/register returns 201 + user id; duplicate… | Authenticated me endpoint, Login issues token, Register user via API | 2 | IdentityControllerTest.java, IdentityUseCaseTests.groovy |
| 2 | POST /identity/login with correct credentials returns a… | Authenticated me endpoint, Login issues token, Register user via API | 2 | IdentityControllerTest.java, IdentityUseCaseTests.groovy |
| 3 | GET /identity/me with the token returns the user… | Authenticated me endpoint, Login issues token, Register user via API | 2 | IdentityControllerTest.java, IdentityUseCaseTests.groovy |
| 4 | Registration publishes a UserRegistered domain event | Password hashing, Registration publishes event | 1 | IdentityUseCaseTests.groovy, RegisterCustomerUseCaseTests.groovy, RegisterProductUseCaseTests.groovy |
| 5 | Passwords are never stored or returned in plaintext | Password hashing, Registration publishes event | 1 | IdentityUseCaseTests.groovy, RegisterCustomerUseCaseTests.groovy, RegisterProductUseCaseTests.groovy |

## Scenario coverage

- Scenarios: 5 total, 5 with detected test evidence, 0 without
- ✓ `Registration publishes event` (@w1)
- ✓ `Password hashing` (@w1)
- ✓ `Register user via API` (@w2)
- ✓ `Login issues token` (@w2)
- ✓ `Authenticated me endpoint` (@w2)

## Demo script (manual walkthrough)

1. **Register user via API** — run the app, perform the scenario, observe the outcome.

## Gaps

- none — spec is fully traced

Note: 'no evidence' means neither the scenario name nor a
token match was found in test files — verify by hand before
treating it as a real gap. '~' marks weak token-match evidence.

## Outcome follow-up

Optional; this is a post-release learning record, not a finish gate.
- Status: not yet measured
- Owner and review date: (record when known)
- Signal and observed result: (record after release; do not invent)
- Learning or next action: (record after review)