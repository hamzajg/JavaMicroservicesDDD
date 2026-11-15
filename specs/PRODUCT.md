# Product map — JavaMicroservicesDDD

## Features

| Feature | Spec | Stories | Scenarios | Waves | State |
|---|---|---|---|---|---|
| customer-rest-api | specs/customer-rest-api/ | 2 | 5 | 2 | shipped — validated |
| product-rest-api | specs/product-rest-api/ | 2 | 5 | 2 | shipped — validated |

## Shipped

- **customer-rest-api** — first usable surface: REST registration +
  queries over the existing DDD slice. In-memory repo/publisher.
  VALIDATION.md: 5/5 ACs traced, 5/5 scenarios with test evidence.
- **product-rest-api** — second bounded context as its own service:
  `Product.App` + `Product.Api` on :8081, SKU uniqueness, shared
  publisher ports relocated to `Services.Common`. VALIDATION.md:
  5/5 ACs traced.

## Candidate next features

- `identity-rest-api` — third bounded context (register/login/token);
  completes the classic e-shop trio. Largest remaining scope.
- `order-rest-api` — order placement spanning customer+product
  catalogs; needs a cross-context story (saga vs. direct call — ADR
  candidate).
- Persistence adapters (JPA) replacing in-memory repositories.
- Real event bus (RabbitMQ/Kafka) replacing `InMemoryPublisherServices`.
