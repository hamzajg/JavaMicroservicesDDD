# AGENTS.md — JavaMicroservicesDDD

Guidance for AI agents (and humans) working in this repository.

## Project

A small multi-module Spring Boot project exploring microservices with
Domain-Driven Design in Java. **Learning/reference project** — currently
only the Customer slice exists; Product/Identity are absent by design.
Stack: Java 8 (Customer.Api) / Java library modules, Spring Boot 2.1.9
(Web + WebFlux deps), Spock + JUnit 4 tests, Gradle 5.6.1 wrapper.

## Commands

Verified 2026-11-13 against CI (`.github/workflows/ci.yml`) and README.

| Task | Command |
|---|---|
| Build + test | `sh gradlew build --no-daemon` (CI: JDK 11) |
| Tests only | `sh gradlew test` |
| Run API | `sh gradlew :Customer.Api:bootRun` (Spring default :8080) |

Requires: **JDK 8 or 11** — Gradle 5.6.1 does not support Java 17; CI pins
`java-version: "11"`. Modules map to dirs via `settings.gradle`'s
`projectDir` trick (names ≠ paths).

## Architecture

```
src/
  Services.Common/    shared DDD kernel (Java):
                      Commands/ICommand + ICommandHandler,
                      Domain/Entity + AggregateRoot,
                      Domain/Events/IEvent + DomainEvent + IEventHandler
  Customer.App/       customer domain + application (Java + Groovy tests):
                      Domain/  Customer, FullName, Email, Address, PhoneNumber
                      Events/  CustomerCreated
                      UseCases/ IRegisterCustomerUseCase, RegisterCustomer,
                               RegisterCustomerUseCase
                      Services/ IPublisherServices + InMemory impl
  Customer.Api/       Spring Boot 2.1.9 shell — Application.java ONLY,
                      NO controllers yet (web + webflux deps present)
tests live in each module's src/test (Spock specs in Customer.App)
```

### API contract

**None yet.** `Customer.Api` has no `@RestController` — grep finds none.
The API surface is unimplemented; the domain/use-case layer is the real
content. Don't document endpoints that don't exist.

## Conventions

- DDD layering by module: `Services.Common` = shared kernel primitives,
  `*.App` = domain + use cases (framework-light), `*.Api` = Spring host.
- Use-case naming: `IRegisterCustomerUseCase` interface +
  `RegisterCustomerUseCase` impl + `RegisterCustomer` command — match it.
- Spock specifications (`.groovy` under `src/test/groovy`) for App tests;
  JUnit for Api tests.
- In-memory publisher (`InMemoryPublisherServices`) is the event bus
  stand-in — no real messaging.
- KISS: this is a learning repo; the layering is the lesson.

## Landmines

- **Default branch is `master`, not `main`.**
- **Gradle 5.6.1 ⇒ JDK ≤11** — Java 17 breaks the build; CI pins 11.
- Spring Boot 2.1.9 / Java 8 — EOL stack, accepted for the course context.
- `Customer.Api` is a **shell with zero endpoints** — README's "REST API"
  framing is aspirational; the implemented part is the domain/use cases.
- `settings.gradle` maps `:Services.Common` etc. to `src/...` dirs —
  module names ≠ directory names.
- Package roots are inconsistent (`com.hamzajg.customer.api` vs
  `Customer.App`, `Services.Common`) — legacy course layout; don't
  "normalise" casually.

## Agent rules

- Branch from `master`; name branches `feat/*`, `fix/*`, `chore/*`, `docs/*`.
- Docs-only changes still go through a PR; never commit to `master` directly.
- Verify before claiming: `sh gradlew build --no-daemon` on JDK ≤11 and
  report the real result.
- Keep changes minimal and consistent with surrounding code. No drive-by
  refactors.
- When adding endpoints: controller in `Customer.Api` calling the
  `*.App` use cases — keep the layering.
- Update AGENTS.md / docs/ARCHITECTURE.md when modules, endpoints, or
  commands change.
