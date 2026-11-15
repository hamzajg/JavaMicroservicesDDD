# JavaMicroservicesDDD

A small multi-module Spring Boot project exploring microservices with
Domain-Driven Design in Java.

## Modules

| Module | Role |
|---|---|
| `Services.Common` | Shared DDD kernel: `Entity`, `AggregateRoot`, commands, domain events, publisher ports |
| `Customer.App` / `Product.App` | Bounded-context domain + application layers (framework-free) |
| `Customer.Api` / `Product.Api` / `Identity.Api` | Spring Boot hosts: REST controllers, DTOs, bean wiring |

Four services: **Customer.Api :8080**, **Product.Api :8081**,
**Identity.Api :8082**, **Order.Api :8083** (places orders by calling
the customer and product services — see `customer.api.url` /
`product.api.url` properties).

## Tech stack

- Java 17
- Spring Boot 2.7.18 (Spring MVC — `spring-boot-starter-web`; WebFlux dep retained but unused)
- Spock 2 + Groovy 3 (`Customer.App`), JUnit 5 (`Customer.Api`)
- Gradle 7.6.4 (wrapper included)

## REST APIs

**Customer service** (`:8080`) — `specs/customer-rest-api/`

| Endpoint | Description |
|---|---|
| `POST /customers` | Register a customer. Body: `firstName`, `lastName`, `email`, `address`, `phoneNumber`. `201` + customer; `400` on invalid input. |
| `GET /customers` | List all registered customers. |
| `GET /customers/{id}` | Get one customer; `404` if unknown. |

**Order service** (`:8083`) — `specs/order-rest-api/` — the first
cross-context feature: it calls Customer (`:8080`) and Product (`:8081`)
over HTTP through hexagonal ports to validate and price orders
server-side.

| Endpoint | Description |
|---|---|
| `POST /orders` | Place an order. Body: `customerId`, `items:[{productId, quantity}]`. `201` + order (lines carry catalog sku/name/unitPrice, `total` computed); `404` unknown customer/product; `400` bad quantities. |
| `GET /orders` | List all orders. |
| `GET /orders/{id}` | Get one order; `404` if unknown. |
| `POST /orders/{id}/cancel` | `PLACED`→`CANCELLED`; `409` if already cancelled; `404` unknown. |

Upstream calls to Customer/Product are bounded by
`upstream.connect-timeout-ms` (default 2000) and
`upstream.read-timeout-ms` (default 5000). A stalled upstream → `504`,
unreachable/5xx → `502`, never a hang or opaque 500
(`specs/order-resilience/`).

**Identity service** (`:8082`) — `specs/identity-rest-api/`

| Endpoint | Description |
|---|---|
| `POST /identity/register` | Register a user (`email`, `displayName`, `password` ≥ 8). `201` + user; `400` invalid; `409` duplicate email. Password stored as salted SHA-256 — **kata-grade crypto, not production** (use BCrypt/Argon2 in real systems). |
| `POST /identity/login` | `email` + `password` → `200` + opaque `token`; `401` on bad credentials. |
| `GET /identity/me` | `Authorization: Bearer <token>` → user; `401` missing/invalid token. |

**Product service** (`:8081`) — `specs/product-rest-api/`

| Endpoint | Description |
|---|---|
| `POST /products` | Register a product. Body: `sku`, `name`, `description`, `price`. `201` + product; `400` invalid, `409` duplicate SKU. |
| `GET /products` | List all registered products. |
| `GET /products/{id}` | Get one product; `404` if unknown. |

Persistence is **in-memory** in both services — restart loses data; this
is the documented MVP contract, not a bug. Registration publishes
`CustomerCreated` / `ProductCreated` domain events to each service's
`InMemoryPublisherServices`.

Examples:

```bash
curl -X POST localhost:8080/customers -H 'Content-Type: application/json' \
  -d '{"firstName":"Jane","lastName":"Roe","email":"jane@example.com","address":"9 Oak Ave","phoneNumber":"555-9999"}'

curl -X POST localhost:8081/products -H 'Content-Type: application/json' \
  -d '{"sku":"WIDGET-1","name":"Super Widget","description":"Does widget things","price":29.99}'
```

## Project layout

```
src/
  Services.Common/   # shared DDD kernel + publisher ports
  Customer.App/      # customer domain + use cases + in-memory repo
  Customer.Api/      # customer REST host (:8080)
  Product.App/       # product domain + use cases + in-memory repo
  Product.Api/       # product REST host (:8081)
  Identity.App/      # identity domain + credentials + token store
  Identity.Api/      # identity REST host (:8082)
  Order.App/         # order domain + CustomerDirectory/ProductCatalog ports
  Order.Api/         # order REST host (:8083) + HTTP port adapters
specs/               # spec-driven feature artifacts (SPEC/EXAMPLES/BACKLOG/WAVES/VALIDATION)
gradlew, gradlew.bat
settings.gradle
```

## Building and running

Requires **JDK 17**:

```bash
./gradlew build                          # compile + test all modules
./gradlew :Customer.Api:bootRun          # customer API on :8080
./gradlew :Product.Api:bootRun           # product API on :8081 (separate terminal)
./gradlew :Identity.Api:bootRun          # identity API on :8082 (separate terminal)
./gradlew :Order.Api:bootRun             # order API on :8083 (needs :8080+:8081 up)
```

## Tests

```bash
./gradlew test
```

Spock specifications live in `Customer.App/src/test/groovy`; JUnit 5 tests
(MockMvc) in `Customer.Api/src/test/java`.

## Status

Working MVP prototype: four bounded contexts — Customer (:8080),
Product (:8081), Identity (:8082), Order (:8083) — each a complete
vertical of REST → use case → aggregate → repository → domain event,
with Order demonstrating cross-service calls via hexagonal ports.
The classic e-shop flow (register → catalog → order) works end to end.
