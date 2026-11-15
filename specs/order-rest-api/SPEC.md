# Spec: order-rest-api

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

Three bounded contexts now exist (Customer :8080, Product :8081,
Identity :8082) but nothing connects them — there is no way to place an
order. `specs/PRODUCT.md` names `order-rest-api` as next; it is the first
feature that must answer a real architectural question: **how does one
service consume another?** Options were direct REST calls vs. domain
events; a saga is overkill at this scale. Direct synchronous calls with
server-side price resolution is the honest MVP — the client must never
supply prices.

## Users

- API consumers (a future storefront) placing orders against real
  customer and product data.
- Developers learning hexagonal wiring: `Order.App` defines ports,
  `Order.Api` adapts them to HTTP.

## Scope (in)

- New `Order.App` module: `Order` aggregate (UUID id, customerId,
  `OrderLine`s with productId/sku/name/qty/unitPrice snapshot, status
  `PLACED`/`CANCELLED`, computed total), `OrderPlaced`/`OrderCancelled`
  events, `OrderRepository` + in-memory impl.
- Ports in `Order.App`: `CustomerDirectory` (`customerExists(UUID)`),
  `ProductCatalog` (`findProduct(UUID)` → snapshot or empty).
- Use cases: `PlaceOrder` (customer must exist → 404; every product
  must exist → 404; qty ≥ 1; price taken from catalog, never client;
  total computed; persists + publishes `OrderPlaced`),
  `CancelOrder` (PLACED → CANCELLED, publishes `OrderCancelled`;
  already-cancelled → conflict), `GetOrder`, `ListOrders`.
- New `Order.Api` module (`:8083`): `OrderConfig` wires ports via
  `RestTemplate` adapters to `http://localhost:8080/customers/{id}` and
  `http://localhost:8081/products/{id}`; `POST/GET /orders`,
  `GET /orders/{id}`, `POST /orders/{id}/cancel`.
- Spock specs for use cases (fake ports); MockMvc specs for the
  controller (fake ports via test config).

## Scope (out)

- Payment, inventory reservation, order queries by customer, saga/
  compensation, retries/timeouts beyond RestTemplate defaults,
  inter-service auth (Identity tokens not yet enforced anywhere).
- Wiring Order into Customer/Product services (decoupled by design).

## Acceptance criteria

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | POST /orders with a known customer + products returns 201 + order id, lines priced server-side, total computed | Place order via API |
| 2 | Unknown customer or product → 404; bad quantities → 400; nothing stored | Place order rejected cleanly |
| 3 | GET /orders lists; GET /orders/{id} returns one or 404 | Query orders |
| 4 | POST /orders/{id}/cancel flips PLACED→CANCELLED; cancelling twice → 409 | Cancel order |
| 5 | Placing publishes OrderPlaced; cancelling publishes OrderCancelled | Order events published |

## Decisions

- **Synchronous REST between services** (ports + RestTemplate adapters)
  over events/saga: simplest correct thing at MVP scale; the port
  interfaces are the seam a future event-driven design would replace.
  Recorded here as the architectural decision.
- **Server-side pricing**: `ProductCatalog` port returns the catalog
  price; the request only carries productId + quantity. Client-supplied
  prices are ignored by design (not even accepted in the DTO).
- Order lines **snapshot** sku/name/price at placement — catalog changes
  must not rewrite order history (same lesson as ddd-eshop ADR 0003).
- Ports live in `Order.App` (hexagonal): `CustomerDirectory`,
  `ProductCatalog`. `Order.Api` implements them over HTTP — App stays
  framework-free.
- `Order.Api` on **:8083**; all four services runnable simultaneously.
