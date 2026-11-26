# AGENTS.md — JavaMicroservicesDDD

Guidance for AI agents (and humans) working in this repository.

## Project

A small multi-module Spring Boot project exploring microservices with
Domain-Driven Design in Java. **Learning/reference project** — currently
only the Customer slice exists; Product/Identity are absent by design.
Stack: Java 25, Spring Boot 3.5.16
(Web + WebFlux deps), Spock 2 + JUnit 5 tests, Gradle 9.9.1 wrapper.

## Commands

Verified 2026-11-13 against CI (`.github/workflows/ci.yml`) and README.

| Task | Command |
|---|---|
| Build + test | `sh gradlew build --no-daemon` (CI: JDK 25) |
| Tests only | `sh gradlew test` |
| Run API | `sh gradlew :Customer.Api:bootRun` (Spring default :8080) |

Requires: **JDK 25** (CI pins `java-version: "25"`; Gradle 9.9.1
runs on it). Modules map to dirs via `settings.gradle`'s
`projectDir` trick (names ≠ paths).

## Architecture

```
src/
  Services.Common/    shared DDD kernel (Java):
                      Commands/ICommand + ICommandHandler,
                      Domain/Entity + AggregateRoot,
                      Domain/Events/IEvent + DomainEvent + IEventHandler,
                      Services/IPublisherServices + InMemory impl
  Customer.App/       customer domain + application (Java + Groovy tests):
                      Domain/  Customer, FullName, Email, Address, PhoneNumber
                      Events/  CustomerCreated
                      UseCases/ IRegisterCustomerUseCase, RegisterCustomer,
                               RegisterCustomerUseCase, GetCustomerUseCase,
                               ListCustomersUseCase
                      Repositories/ CustomerRepository + InMemory impl
  Customer.Api/       customer Spring Boot host (:8080) — Application.java,
                      CustomerConfig (@Bean wiring), CustomerController,
                      CustomerRequest/CustomerResponse DTOs
  Product.App/        product bounded context (mirrors Customer.App):
                      Domain/Product, Events/ProductCreated,
                      UseCases/ Register/Get/List + DuplicateSkuException,
                      Repositories/ ProductRepository + InMemory impl
  Product.Api/        product Spring Boot host (:8081) — same shape as
                      Customer.Api (ProductConfig/Controller/DTOs)
  Identity.App/       identity bounded context: Domain/User (email,
                      displayName, salted SHA-256 hash+salt), Events/
                      UserRegistered, Services/PasswordHasher + AuthToken,
                      Repositories/UserRepository + AuthTokenStore +
                      in-memory impls, UseCases/Register+Login+Authenticate
  Identity.Api/       identity Spring Boot host (:8082) —
                      IdentityConfig/Controller/DTOs; register/login/me
  Order.App/          order bounded context: Domain/Order+OrderLine+
                      OrderStatus, Events/OrderPlaced+OrderCancelled,
                      Ports/CustomerDirectory+ProductCatalog (hexagonal),
                      Repositories/OrderRepository+in-memory, UseCases/
                      Place+Cancel+Get+List
  Order.Api/          order Spring Boot host (:8083) — OrderConfig,
                      OrderController, RemotePorts (RestTemplate adapters
                      to :8080/:8081, URLs via customer.api.url /
                      product.api.url), UpstreamException, auth/
                      (BearerAuthFilter + TokenIntrospector → Identity
                      /identity/me via identity.api.url), DTOs
tests live in each module's src/test (Spock specs in Customer.App)
```

### API contract

`CustomerController` (`com.hamzajg.customer.api`, `@RequestMapping("/customers")`):

| Endpoint | Result |
|---|---|
| `POST /customers` | 201 + `CustomerResponse`; 400 on `IllegalArgumentException` from use-case validation |
| `GET /customers` | 200 + list of `CustomerResponse` |
| `GET /customers/{id}` | 200 + `CustomerResponse`; 404 unknown id |

**Product service** (`ProductController`, `:8081`, `/products`) — same
shape: `POST` → 201 / 400 / **409** (`DuplicateSkuException`),
`GET` list, `GET /{id}` → 404.

Persistence/events/tokens are in-memory (`InMemory*Repository`,
`InMemoryAuthTokenStore`, `InMemoryPublisherServices` in
`Services.Common`) wired per-service via `*Config` `@Bean`s — `*.App`
modules stay Spring-free. Publisher ports live in `Services.Common`,
NOT in a `*.App` module — don't re-create them per context.

## Conventions

- DDD layering by module: `Services.Common` = shared kernel primitives,
  `*.App` = domain + use cases (framework-light), `*.Api` = Spring host.
- Use-case naming: `IRegisterCustomerUseCase` interface +
  `RegisterCustomerUseCase` impl + `RegisterCustomer` command — match it.
- Spock 2 specifications (`.groovy` under `src/test/groovy`) for App tests
  (JUnit platform); JUnit 5 (`jupiter`) for Api tests.
- In-memory publisher (`InMemoryPublisherServices`) is the event bus
  stand-in — no real messaging.
- KISS: this is a learning repo; the layering is the lesson.

## Landmines

- **Default branch is `master`, not `main`.**
- Spring Boot 3.5.16 / Java 25 — modernized 2026-11 (was Boot 2.1.9/Java 8,
  Gradle 5.6.1/JDK 11); the namespace moved to `jakarta.*` with it.
- `Order.Api` runs on **8083** and needs Customer (:8080) + Product
  (:8081) reachable — `customer.api.url`/`product.api.url` properties.
  Upstream calls are bounded by `upstream.connect-timeout-ms` /
  `upstream.read-timeout-ms`; failures surface as 502/504 via
  `UpstreamException`, never hangs or opaque 500s.
- **All `/orders` endpoints require a Bearer token** issued by Identity
  (`POST /identity/login`); Order.Api introspects via
  `GET /identity/me` (`identity.api.url`, default :8082). Missing/invalid
  → 401, Identity down → 503. Live demos must login first; the other
  APIs remain unauthenticated.
- **Orders are owned by the caller** (`specs/order-ownership/`): the token
  email resolves to a customer via `GET /customers/by-email`; `POST /orders`
  derives `customerId` from that (never the body), `GET /orders` is scoped
  to the caller, and foreign orders read/cancel as `404`. Ownership is
  enforced in `OrderController`; `Order.App` adds `findByCustomerId` +
  `ResolveCustomerUseCase`.
- `Identity.Api` runs on **8082**; all services can run
  simultaneously (separate terminals — see bootRun landmine).
- `Product.Api` runs on **8081** (`application.properties`) so both
  services run side by side; `gradlew :A:bootRun :B:bootRun` runs them
  SEQUENTIALLY — use two terminals/processes for a live pair.
- Each `*.Api` module must declare `test { useJUnitPlatform() }` —
  without it JUnit 5 tests are **silently skipped** (this hid a real
  `PhoneNumber` ctor bug until the flag was added).
- `.gitignore` ignores `build/` at any depth — module build output must
  never be committed (a new module's `build/` slipped into PR #11 once).
- `settings.gradle` maps `:Services.Common` etc. to `src/...` dirs —
  module names ≠ directory names.
- Package roots are inconsistent (`com.hamzajg.customer.api` vs
  `Customer.App`, `Services.Common`) — legacy course layout; don't
  "normalise" casually.

## Agent rules

- Branch from `master`; name branches `feat/*`, `fix/*`, `chore/*`, `docs/*`.
- Docs-only changes still go through a PR; never commit to `master` directly.
- Verify before claiming: `sh gradlew build --no-daemon` on JDK 25 and
  report the real result.
- Keep changes minimal and consistent with surrounding code. No drive-by
  refactors.
- When adding endpoints: controller in `Customer.Api` calling the
  `*.App` use cases — keep the layering.
- Update AGENTS.md / docs/ARCHITECTURE.md when modules, endpoints, or
  commands change.
