# Waves — customer-rest-api

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- Registration publishes event
- Customer gets server id

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As a developer, I want the customer use case to validate, persist and identify customers, so that registration actually works | M | — | Customer UUID id via AggregateRoot ctor, CustomerRepository + InMemory impl, validation in RegisterCustomerUseCase, Get/List use cases, Spock specs. |

## Wave 2 — iteration 2

Scenarios proven by this wave:
- Register customer via API
- Register invalid customer rejected
- Query customers

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US2 | As an API consumer, I want REST endpoints for customer registration and queries, so that I can use the service over HTTP | M | US1 | CustomerController (POST/GET/GET{id}), DTO mapping, @Configuration bean wiring, MockMvc tests, README/AGENTS refresh. |
