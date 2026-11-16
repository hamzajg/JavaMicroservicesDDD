# Waves — order-ownership

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- The caller identity is read from the token
- A caller is resolved to their customer by email
- An unknown caller email resolves to no customer

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As an API consumer, I want Order.Api to know which customer is calling, so that orders can be scoped to their owner | M | — | identity-aware introspection (email from /identity/me) + CustomerDirectory.findByEmail over a new Customer lookup-by-email endpoint; Order.App gains the port, Customer.App/Api gain findByEmail + GET /customers/by-email. No order behaviour changes yet. |

## Wave 2 — iteration 2

Scenarios proven by this wave:
- Placing an order uses the caller's identity
- Listing returns only the caller's orders
- Another customer's order is invisible

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US2 | As a shopper, I want to place and see only my own orders, so that no other authenticated caller can read or cancel them | M | US1 | POST /orders drops customerId (derived from the token); GET /orders filters by the caller's customer; GET /{id} + cancel return 404 for foreign orders. Ownership enforced in the controller. |
