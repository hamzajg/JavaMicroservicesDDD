# Backlog: customer-rest-api

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

## US1 — As a developer, I want the customer use case to validate, persist and identify customers, so that registration actually works
- Scenarios: @w1
- Size: M
- Notes: Customer UUID id via AggregateRoot ctor, CustomerRepository + InMemory impl, validation in RegisterCustomerUseCase, Get/List use cases, Spock specs.

## US2 — As an API consumer, I want REST endpoints for customer registration and queries, so that I can use the service over HTTP
- Scenarios: @w2
- Depends: US1
- Size: M
- Notes: CustomerController (POST/GET/GET{id}), DTO mapping, @Configuration bean wiring, MockMvc tests, README/AGENTS refresh.
