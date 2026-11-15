# Spec: product-rest-api

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

The repo is named *JavaMicroservicesDDD* but only the Customer bounded
context exists — `specs/PRODUCT.md` lists `product-rest-api` as the next
candidate. There is no Product aggregate, no product module, and no way
to manage a catalog. Meanwhile `IPublisherServices` /
`InMemoryPublisherServices` live inside `Customer.App` even though they
are generic ports every bounded context needs — Product would have to
duplicate them.

## Users

- API consumers building the catalog side of the e-shop (pairs with the
  customer service).
- Developers learning the multi-context DDD layout this repo teaches.

## Scope (in)

- Move `IPublisherServices` + `InMemoryPublisherServices` from
  `Customer.App` to `Services.Common` (shared kernel) and update
  references.
- New `Product.App` module (`src/Product.App`, registered in
  `settings.gradle`): `Product` aggregate (UUID id, sku, name,
  description, `BigDecimal` price), `ProductCreated` event,
  `ProductRepository` + in-memory impl, `RegisterProduct` /
  `GetProduct` / `ListProducts` use cases.
- SKU uniqueness enforced at registration (repository lookup).
- New `Product.Api` module (`src/Product.Api`): Spring Boot app on
  `server.port=8081`, `POST/GET /products`, `GET /products/{id}`,
  201/400/404/409 semantics, `@Bean` wiring, MockMvc tests.
- Spock specs for Product.App use cases.

## Scope (out)

- Ordering/cart, product updates/deletes, pagination, real persistence.
- Customer↔Product integration (contexts stay decoupled).
- Moving Customer.Api off port 8080.

## Acceptance criteria

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | POST /products with valid data returns 201 + id; retrievable by id | Register product via API |
| 2 | Missing/invalid fields or duplicate SKU rejected (400/409), nothing stored | Register invalid product rejected |
| 3 | GET /products lists; GET /products/{id} returns one or 404 | Query products |
| 4 | Registration publishes a ProductCreated domain event | Registration publishes event |
| 5 | Customer service still builds and its publisher import resolves from Services.Common | Shared publisher relocation |

## Decisions

- Two new Gradle modules mirror the Customer layout — each bounded
  context gets its own `*.App` + `*.Api`, the actual microservices
  pattern this repo name promises. `Product.Api` runs on 8081 so both
  services can run side by side.
- Publisher ports move to `Services.Common` rather than duplicating —
  they are generic application ports, and the shared kernel exists for
  exactly this.
- Price is a plain `BigDecimal` (single currency implied) — a `Money`
  VO is deferred until multi-currency exists.
- SKU uniqueness lives in the use case via `findBySku` — domain rule,
  not a DB constraint (in-memory MVP).
