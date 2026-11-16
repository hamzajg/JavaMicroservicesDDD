# Backlog: order-ownership

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

## US1 — As an API consumer, I want Order.Api to know which customer is calling, so that orders can be scoped to their owner
- Scenarios: @w1
- Size: M
- Notes: identity-aware introspection (email from /identity/me) + CustomerDirectory.findByEmail over a new Customer lookup-by-email endpoint; Order.App gains the port, Customer.App/Api gain findByEmail + GET /customers/by-email. No order behaviour changes yet.

## US2 — As a shopper, I want to place and see only my own orders, so that no other authenticated caller can read or cancel them
- Scenarios: @w2
- Depends: US1
- Size: M
- Notes: POST /orders drops customerId (derived from the token); GET /orders filters by the caller's customer; GET /{id} + cancel return 404 for foreign orders. Ownership enforced in the controller.
