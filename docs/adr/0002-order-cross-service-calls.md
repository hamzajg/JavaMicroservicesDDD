# ADR 0002 — Cross-service ordering via synchronous REST ports

## Status

Accepted (2026-11-15)

## Context

`order-rest-api` is the first feature spanning bounded contexts. Placing
an order requires two facts owned by other services: the customer must
exist (Customer.Api :8080) and each product's catalog price must be
resolved (Product.Api :8081). The client must never supply prices.
Constraints: in-memory MVP, no message broker, no service mesh, small
team, decision must stay reversible.

## Decision

`Order.App` defines two ports — `CustomerDirectory.customerExists(UUID)`
and `ProductCatalog.findById(UUID)` — and `Order.Api` adapts them to
synchronous REST calls (`RestTemplate`) against the other services.
Order lines snapshot sku/name/unitPrice at placement; the order total is
computed server-side.

## Alternatives considered

1. **Synchronous REST via ports (chosen)** — smallest correct step;
  ports keep `Order.App` framework-free and are the seam an event-driven
  design would replace.
2. **Domain events / saga** — Customer/Product publish facts, Order
  consumes asynchronously. Rejected for MVP: no broker exists, eventual
  consistency complicates "does this customer exist right now?" and
  price-at-order-time semantics, and it is a much larger build.
3. **Client supplies prices + ids** — rejected outright: prices are
  catalog-owned; trusting the client corrupts totals.
4. **Shared database / direct module calls** — rejected: breaks the
  bounded-context boundary the repo exists to teach.

## Consequences

- Positive: real cross-service validation and pricing; hexagonal ports
  demonstrate the anti-corruption seam; each service stays independently
  deployable; failure surfaces as 404/500 at order time.
- Negative: temporal coupling — Order is down if Customer/Product are
  down; no retries, timeouts, or circuit breaking; in-memory state means
  orders can't survive restarts anyway (accepted MVP limit).
- Reversibility: two-way door — ports can be re-implemented over a
  broker later without touching `Order.App` domain code.

## Forces

MVP scope and zero infrastructure (no RabbitMQ/Kafka deployed),
correctness of prices, teaching value of explicit boundaries,
team size of one.

## Revisit trigger

Reopen when a message broker is introduced to the platform, when order
placement latency or Customer/Product availability becomes a measured
problem, or when a second context needs order data asynchronously.

## Evidence

- `src/Order.App/src/main/java/Order/App/Ports/` — port interfaces
- `src/Order.Api/src/main/java/com/hamzajg/order/api/RemotePorts.java` —
  HTTP adapters (`customer.api.url`, `product.api.url`)
- `specs/order-rest-api/SPEC.md` → Decisions; `VALIDATION.md` 5/5 ACs
- Live demo: order placed against running :8080/:8081/:8083 services
