# Validation — product-rest-api

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | POST /products with valid data returns 201 + id… | Query products, Register invalid product rejected, Register product via API | 2 | ProductControllerTest.java |
| 2 | Missing/invalid fields or duplicate SKU rejected (400/409)… | Query products, Register invalid product rejected, Register product via API | 2 | ProductControllerTest.java |
| 3 | GET /products lists; GET /products/{id} returns one or 404 | Query products, Register invalid product rejected, Register product via API | 2 | ProductControllerTest.java |
| 4 | Registration publishes a ProductCreated domain event | Registration publishes event, Shared publisher relocation | 1 | IdentityUseCaseTests.groovy, RegisterCustomerUseCaseTests.groovy, RegisterProductUseCaseTests.groovy |
| 5 | Customer service still builds and its publisher import… | Registration publishes event, Shared publisher relocation | 1 | IdentityUseCaseTests.groovy, RegisterCustomerUseCaseTests.groovy, RegisterProductUseCaseTests.groovy |

## Scenario coverage

- Scenarios: 5 total, 5 with detected test evidence, 0 without
- ✓ `Registration publishes event` (@w1)
- ✓ `Shared publisher relocation` (@w1)
- ✓ `Register product via API` (@w2)
- ✓ `Register invalid product rejected` (@w2)
- ✓ `Query products` (@w2)

## Demo script (manual walkthrough)

1. **Register product via API** — run the app, perform the scenario, observe the outcome.

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