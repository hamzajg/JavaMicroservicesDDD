# Validation — order-ownership

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | Introspection exposes the caller's email from… | A caller is resolved to their customer by email, An unknown caller email resolves to no customer, The caller identity is read from the token | 1 | BearerAuthFilterTest.java, CustomerControllerTest.java, IdentityTokenIntrospectorTest.java |
| 2 | A caller's email resolves to their customer through… | A caller is resolved to their customer by email, An unknown caller email resolves to no customer, The caller identity is read from the token | 1 | BearerAuthFilterTest.java, CustomerControllerTest.java, IdentityTokenIntrospectorTest.java |
| 3 | `POST /orders` takes no `customerId`; the order is created… | Another customer's order is invisible, Listing returns only the caller's orders, Placing an order uses the caller's identity | 2 | OrderControllerTest.java, OrderUseCaseTests.groovy |
| 4 | `GET /orders` returns only the caller's orders | Another customer's order is invisible, Listing returns only the caller's orders, Placing an order uses the caller's identity | 2 | OrderControllerTest.java, OrderUseCaseTests.groovy |
| 5 | Reading or cancelling another customer's order returns `404` | Another customer's order is invisible, Listing returns only the caller's orders, Placing an order uses the caller's identity | 2 | OrderControllerTest.java, OrderUseCaseTests.groovy |

## Scenario coverage

- Scenarios: 6 total, 6 with detected test evidence, 0 without
- ✓ `The caller identity is read from the token` (@w1)
- ✓ `A caller is resolved to their customer by email` (@w1)
- ✓ `An unknown caller email resolves to no customer` (@w1)
- ✓ `Placing an order uses the caller's identity` (@w2)
- ✓ `Listing returns only the caller's orders` (@w2)
- ✓ `Another customer's order is invisible` (@w2)

## Demo script (manual walkthrough)

1. **The caller identity is read from the token** — run the app, perform the scenario, observe the outcome.
2. **A caller is resolved to their customer by email** — run the app, perform the scenario, observe the outcome.
3. **Placing an order uses the caller's identity** — run the app, perform the scenario, observe the outcome.

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