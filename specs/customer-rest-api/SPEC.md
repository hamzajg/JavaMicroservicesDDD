# Spec: customer-rest-api

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

The Customer service is a DDD skeleton with no usable surface: the domain
aggregate exists (`Customer`, `FullName`, `Email`, `Address`,
`PhoneNumber`), `RegisterCustomerUseCase` validates nothing, **never
stores the customer** (it publishes the last domain event and drops the
aggregate), assigns no id, and `Customer.Api` exposes zero endpoints —
the only test is `contextLoads`. There is no way to register or look up a
customer over HTTP, and no persistence at all.

## Users

- API consumers (other services, the future web front-end) that need to
  register customers and query them.
- Developers extending the kata — the service should demonstrate the
  full vertical slice: command → use case → domain → repository → event.

## Scope (in)

- `Customer` gets a server-generated `UUID` id at construction.
- `CustomerRepository` interface + `InMemoryCustomerRepository` in
  `Customer.App` (framework-free).
- `RegisterCustomerUseCase` validates required fields (non-blank name
  parts, email containing `@`, non-blank address/phone), persists the
  customer, then publishes `CustomerCreated`.
- Query use cases: `GetCustomerUseCase` (by id), `ListCustomersUseCase`.
- `CustomerController` in `Customer.Api`: `POST /customers`,
  `GET /customers`, `GET /customers/{id}` with 201/200/400/404 semantics.
- Bean wiring via `@Configuration` in the Api module — `Customer.App`
  stays Spring-free.
- Spock specs for use cases; MockMvc test for the controller.

## Scope (out)

- Real persistence (JPA/JDBC) — in-memory is the documented MVP contract.
- Update/delete customer, duplicate-email rules, authentication.
- Fixing the unusual `*.src.main.java.*` package layout (works; churn not
  justified for this feature).
- WebFlux reactive endpoints — MVC (`spring-boot-starter-web`) is used.

## Acceptance criteria

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | POST /customers with valid data returns 201 and a customer id; the customer is retrievable | Register customer via API |
| 2 | POST with missing/invalid fields returns 400 and stores nothing | Register invalid customer rejected |
| 3 | GET /customers lists registered customers; GET /customers/{id} returns one or 404 | Query customers |
| 4 | Registering publishes a CustomerCreated domain event | Registration publishes event |
| 5 | A registered customer has a non-null UUID id | Customer gets server id |

## Decisions

- Repository interface lives in `Customer.App` next to the use cases;
  in-memory impl also in App (no separate infrastructure module exists).
- Validation lives in the use case (command-level), not the value
  objects — keeps `FullName`/`Email` etc. anemic as the kata intends.
- Api wiring via `@Configuration` + `@Bean` methods — `Customer.App`
  keeps zero framework dependencies.
