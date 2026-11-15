# Validation — product-rest-api

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | POST /products with valid data returns 201 + id; retrievable | 3 | @w2 | ProductControllerTest.java |
| 2 | Missing/invalid fields or duplicate SKU rejected (400/409),  | 3 | @w2 | ProductControllerTest.java |
| 3 | GET /products lists; GET /products/{id} returns one or 404 | 3 | @w2 | ProductControllerTest.java |
| 4 | Registration publishes a ProductCreated domain event | 2 | @w1 | RegisterCustomerUseCaseTests.groovy, RegisterProductUseCaseTests.groovy |
| 5 | Customer service still builds and its publisher import resol | 2 | @w1 | RegisterCustomerUseCaseTests.groovy, RegisterProductUseCaseTests.groovy |

## Scenario coverage

- Scenarios: 5 total, 4 with detected test evidence, 1 without
- ✓ `Registration publishes event` (@w1)
- ✗ `Shared publisher relocation` (@w1)
- ✓ `Register product via API` (@w2)
- ✓ `Register invalid product rejected` (@w2)
- ✓ `Query products` (@w2)

## Demo script (manual walkthrough)

1. **Register product via API** — run the app, perform the scenario, observe the outcome.

## Gaps

- 1 scenario(s) lack detected test evidence — verify manually or add checks

Note: 'no evidence' means neither the scenario name nor a
token match was found in test files — verify by hand before
treating it as a real gap. '~' marks weak token-match evidence.