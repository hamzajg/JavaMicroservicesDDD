# Waves — product-rest-api

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- Registration publishes event
- Shared publisher relocation

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As a developer, I want a Product bounded context with shared kernel publisher ports, so that the catalog side exists as its own service | L | — | Move IPublisherServices/InMemoryPublisherServices to Services.Common; new src/Product.App module (Product aggregate, ProductCreated, repo, Register/Get/List use cases, SKU uniqueness + validation); register module in settings.gradle; Spock specs. |

## Wave 2 — iteration 2

Scenarios proven by this wave:
- Register product via API
- Register invalid product rejected
- Query products

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US2 | As an API consumer, I want a Product REST service on its own port, so that catalog management is independently deployable | M | US1 | New src/Product.Api module (Boot app :8081, /products endpoints, DTOs, @Bean wiring, MockMvc tests); README/AGENTS/PRODUCT refresh. |
