# Validation — order-rest-api

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | POST /orders with a known customer + products returns 201 +  | 4 | @w2 | OrderControllerTest.java |
| 2 | Unknown customer or product → 404; bad quantities → 400; not | 4 | @w2 | OrderControllerTest.java |
| 3 | GET /orders lists; GET /orders/{id} returns one or 404 | 4 | @w2 | OrderControllerTest.java |
| 4 | POST /orders/{id}/cancel flips PLACED→CANCELLED; cancelling  | 4 | @w2 | OrderControllerTest.java |
| 5 | Placing publishes OrderPlaced; cancelling publishes OrderCan | 1 | @w1 | OrderUseCaseTests.groovy |

## Scenario coverage

- Scenarios: 5 total, 5 with detected test evidence, 0 without
- ✓ `Order events published` (@w1)
- ✓ `Place order via API` (@w2)
- ✓ `Place order rejected cleanly` (@w2)
- ✓ `Query orders` (@w2)
- ✓ `Cancel order` (@w2)

## Demo script (manual walkthrough)

1. **Place order via API** — run the app, perform the scenario, observe the outcome.

## Gaps

- none — spec is fully traced

Note: 'no evidence' means neither the scenario name nor a
token match was found in test files — verify by hand before
treating it as a real gap. '~' marks weak token-match evidence.