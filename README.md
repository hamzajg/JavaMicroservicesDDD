# JavaMicroservicesDDD

A small multi-module Spring Boot project exploring microservices with
Domain-Driven Design in Java.

## Modules

| Module | Role |
|---|---|
| `Services.Common` | Shared DDD kernel: `Entity`, `AggregateRoot`, commands, domain events |
| `Customer.App` | Customer domain + application layer (framework-free): aggregate, value objects, `CustomerCreated` event, repository, use cases |
| `Customer.Api` | Spring Boot host: REST controller, DTOs, bean wiring |

## Tech stack

- Java 17
- Spring Boot 2.7.18 (Spring MVC — `spring-boot-starter-web`; WebFlux dep retained but unused)
- Spock 2 + Groovy 3 (`Customer.App`), JUnit 5 (`Customer.Api`)
- Gradle 7.6.4 (wrapper included)

## Customer REST API

| Endpoint | Description |
|---|---|
| `POST /customers` | Register a customer. Body: `firstName`, `lastName`, `email`, `address`, `phoneNumber`. Returns `201` + customer (`id`, `fullName`, `email`, `address`, `phoneNumber`); `400` on invalid input. |
| `GET /customers` | List all registered customers. |
| `GET /customers/{id}` | Get one customer; `404` if unknown. |

Persistence is **in-memory** (`InMemoryCustomerRepository`) — restart loses
data; this is the documented MVP contract, not a bug. Registration publishes
a `CustomerCreated` domain event to `InMemoryPublisherServices`.

Example:

```bash
curl -X POST localhost:8080/customers -H 'Content-Type: application/json' \
  -d '{"firstName":"Jane","lastName":"Roe","email":"jane@example.com","address":"9 Oak Ave","phoneNumber":"555-9999"}'
```

## Project layout

```
src/
  Services.Common/   # shared DDD kernel
  Customer.App/      # domain + use cases + in-memory repo/publisher
  Customer.Api/      # Spring Boot REST host
specs/               # spec-driven feature artifacts (SPEC/EXAMPLES/BACKLOG/WAVES/VALIDATION)
gradlew, gradlew.bat
settings.gradle
```

## Building and running

Requires **JDK 17**:

```bash
./gradlew build                # compile + test all modules
./gradlew :Customer.Api:bootRun
```

The API starts on `http://localhost:8080`.

## Tests

```bash
./gradlew test
```

Spock specifications live in `Customer.App/src/test/groovy`; JUnit 5 tests
(MockMvc) in `Customer.Api/src/test/java`.

## Status

Working MVP prototype: the customer slice is a complete vertical —
REST → use case → aggregate → repository → domain event. The `Product`
and `Identity` slices of a typical e-shop are not present here.
