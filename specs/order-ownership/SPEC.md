# Spec: order-ownership

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

`order-auth` proved the caller holds a valid Identity token, but it used the
token as a boolean: `IntrospectionResult` is `VALID/INVALID/UNAVAILABLE` and
`IdentityTokenIntrospector` throws away the body of `GET /identity/me`.
So the caller's *identity* never reaches order logic — any authenticated
caller can list every order, read or cancel any order by id, and place an
order for an arbitrary `customerId`. Orders are the customer-data-bearing
surface of the system; they must belong to the caller. `order-auth` named
this exact hole in its scope-out notes ("Roles/scopes/authorization beyond
'valid token' (no RBAC)").

## Users

- Registered shoppers who hold a token from `POST /identity/login` and
  expect to see and manage only their own orders.
- Operators who need order data to be unreadable/unwritable across
  customers before this can ever face a storefront.

## Outcome hypothesis

- Expected user outcome: a shopper can place an order and list/read/cancel
  it, and no other authenticated caller can see or touch it.
- Opportunity evidence: `specs/order-auth/SPEC.md` scope-out #2 (deferred by
  design); `specs/order-rest-api/SPEC.md` scope-out ("order queries by
  customer"). This is the documented next step, not a guess.
- Signal: today `GET /orders` returns orders for every customer; after this
  feature it returns only the caller's. Observable via a two-token API walk.
- Follow-up owner/date: repo maintainer, next `order-*` spec.

## Scope (in)

- Identity-aware introspection: `TokenIntrospector` returns the caller's
  email (parsed from `GET /identity/me`) alongside the outcome; the
  `BearerAuthFilter` attaches the caller identity to the request.
- Caller → customer resolution: `CustomerDirectory.findByEmail(email)` port
  in `Order.App`; `Customer.App` gains `CustomerRepository.findByEmail` +
  a find-by-email use case; `Customer.Api` exposes
  `GET /customers/by-email?email=...`; `RemotePorts` adapts the port to it.
- `POST /orders` derives the order's `customerId` from the authenticated
  caller — the request body no longer carries a `customerId`, so a client
  can never place an order as someone else.
- `GET /orders` returns only the caller's orders
  (`OrderRepository.findByCustomerId`).
- `GET /orders/{id}` and `POST /orders/{id}/cancel` return `404` when the
  order belongs to another customer (a caller must not learn that someone
  else's order exists).

## Scope (out)

- Roles, scopes, admin/operator overrides — ownership is binary (owner or
  not). No RBAC.
- Auth on Customer/Product/Identity APIs (separate feature; same pattern).
- Token format changes (JWT/refresh), persistence, new services/ports.
- Ownership of customers/products — those APIs stay open.

## Acceptance criteria

<!-- Scenario(s): use scenario names verbatim from EXAMPLES.feature and/or
     @w<N> tags — ship_validate resolves both for traceability. -->

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | Introspection exposes the caller's email from `/identity/me` and the filter attaches it to the request | The caller identity is read from the token |
| 2 | A caller's email resolves to their customer through Customer's lookup-by-email; an unknown email yields no customer (→ 404 before any order work) | A caller is resolved to their customer by email, An unknown caller email resolves to no customer |
| 3 | `POST /orders` takes no `customerId`; the order is created for the caller's customer | Placing an order uses the caller's identity |
| 4 | `GET /orders` returns only the caller's orders | Listing returns only the caller's orders |
| 5 | Reading or cancelling another customer's order returns `404` | Another customer's order is invisible |

## Decisions

- **Identity→customer link is email.** Customer and Identity already both
  carry an email; login email == customer email is the honest MVP linkage.
  Recorded here so a future change (e.g. explicit `customerId` on `User`)
  does not silently revert it. No new cross-service coupling direction:
  Order already calls Customer and Identity.
- **Derive, don't trust.** `POST /orders` drops `customerId` from the body
  and derives it from the token — the same "the client must never supply
  it" rule `order-rest-api` applied to prices.
- **404, not 403, for foreign orders** — do not leak existence of other
  customers' orders (matches `order-rest-api`'s 404-for-unknown).
- **Ownership checks live in `Order.Api`** (controller), like auth itself:
  authorization is an inbound concern; `Order.App` use cases stay unchanged
  for get/cancel and only gain a scoped list + a find-by-email use case.
- **Additive, not breaking, for Customer**: one new repository method, one
  use case, one read-only endpoint; existing Customer behaviour is intact.
