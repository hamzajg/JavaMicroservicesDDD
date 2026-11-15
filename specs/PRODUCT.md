# Product map — JavaMicroservicesDDD

## Features

| Feature | Spec | Stories | Scenarios | Waves | State |
|---|---|---|---|---|---|
| customer-rest-api | specs/customer-rest-api/ | 2 | 5 | 2 | shipped — validated |

## Shipped

- **customer-rest-api** — first usable surface: REST registration +
  queries over the existing DDD slice. In-memory repo/publisher.
  VALIDATION.md: 5/5 ACs traced, 5/5 scenarios with test evidence.

## Candidate next features

- `product-rest-api` — mirror the customer slice for a Product aggregate
  (new bounded context; needs its own domain model).
- `identity-rest-api` — auth/token issuance; larger scope.
- Persistence adapter (JPA) replacing in-memory repositories.
- Real event bus (RabbitMQ/Kafka) replacing `InMemoryPublisherServices`.
