# Backlog: product-rest-api

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

## US1 — As a developer, I want a Product bounded context with shared kernel publisher ports, so that the catalog side exists as its own service
- Scenarios: @w1
- Size: L
- Notes: Move IPublisherServices/InMemoryPublisherServices to Services.Common; new src/Product.App module (Product aggregate, ProductCreated, repo, Register/Get/List use cases, SKU uniqueness + validation); register module in settings.gradle; Spock specs.

## US2 — As an API consumer, I want a Product REST service on its own port, so that catalog management is independently deployable
- Scenarios: @w2
- Depends: US1
- Size: M
- Notes: New src/Product.Api module (Boot app :8081, /products endpoints, DTOs, @Bean wiring, MockMvc tests); README/AGENTS/PRODUCT refresh.
