# Backlog: order-rest-api

Items are USER STORIES in the parseable format below — keep the heading
shape `## US<n> — <story>` and the field lines intact so `wave_plan` can
parse them. Write the story as: "As a <role>, I want <goal>, so that
<value>" — the title after the dash IS the story.

```
## US<n> — As a <role>, I want <goal>, so that <value>
- Scenarios: @w1, @w2
- Depends: US1, US3        (optional; omit for wave-1 items)
- Size: S|M|L
- Notes: <one line>
```

Order stories by user value; dependencies express technical order only.

## US1 — As a developer, I want an Order bounded context with catalog/customer ports, so that orders validate against real services
- Scenarios: @w1
- Size: L
- Notes: Order.App module — Order/OrderLine/OrderStatus aggregate, OrderPlaced/OrderCancelled events, CustomerDirectory + ProductCatalog ports, OrderRepository in-memory, PlaceOrder/CancelOrder/Get/List use cases; Spock specs with fake ports.

## US2 — As an API consumer, I want an Order REST service on :8083 backed by the other services, so that I can place and manage orders end to end
- Scenarios: @w2
- Depends: US1
- Size: M
- Notes: Order.Api module — RestTemplate port adapters to :8080/:8081, /orders endpoints + cancel, DTOs, MockMvc tests with fake ports, README/AGENTS/PRODUCT refresh.
