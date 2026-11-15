# Waves — order-rest-api

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- Order events published

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As a developer, I want an Order bounded context with catalog/customer ports, so that orders validate against real services | L | — | Order.App module — Order/OrderLine/OrderStatus aggregate, OrderPlaced/OrderCancelled events, CustomerDirectory + ProductCatalog ports, OrderRepository in-memory, PlaceOrder/CancelOrder/Get/List use cases; Spock specs with fake ports. |

## Wave 2 — iteration 2

Scenarios proven by this wave:
- Place order via API
- Place order rejected cleanly
- Query orders
- Cancel order

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US2 | As an API consumer, I want an Order REST service on :8083 backed by the other services, so that I can place and manage orders end to end | M | US1 | Order.Api module — RestTemplate port adapters to :8080/:8081, /orders endpoints + cancel, DTOs, MockMvc tests with fake ports, README/AGENTS/PRODUCT refresh. |
