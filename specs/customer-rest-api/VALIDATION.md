# Validation — customer-rest-api

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | POST /customers with valid data returns 201 and a customer i | 3 | @w2 | CustomerControllerTest.java, RegisterCustomerUseCaseTests.groovy |
| 2 | POST with missing/invalid fields returns 400 and stores noth | 3 | @w2 | CustomerControllerTest.java, RegisterCustomerUseCaseTests.groovy |
| 3 | GET /customers lists registered customers; GET /customers/{i | 3 | @w2 | CustomerControllerTest.java, RegisterCustomerUseCaseTests.groovy |
| 4 | Registering publishes a CustomerCreated domain event | 2 | @w1 | RegisterCustomerUseCaseTests.groovy |
| 5 | A registered customer has a non-null UUID id | 2 | @w1 | RegisterCustomerUseCaseTests.groovy |

## Scenario coverage

- Scenarios: 5 total, 5 with detected test evidence, 0 without
- ✓ `Register customer via API` (@w2)
- ✓ `Register invalid customer rejected` (@w2)
- ✓ `Query customers` (@w2)
- ✓ `Registration publishes event` (@w1)
- ✓ `Customer gets server id` (@w1)

## Demo script (manual walkthrough)

1. **Register customer via API** — run the app, perform the scenario, observe the outcome.

## Gaps

- none — spec is fully traced

Note: 'no evidence' means neither the scenario name nor a
token match was found in test files — verify by hand before
treating it as a real gap. '~' marks weak token-match evidence.