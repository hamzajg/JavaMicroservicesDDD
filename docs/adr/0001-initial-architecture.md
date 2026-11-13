# ADR 0001 — Initial architecture

Date: 2026-11-13 (baseline recorded retroactively)
Status: accepted (describes what is built)

## Context

A course-style exploration of microservices + DDD in Java/Spring. Only the
Customer slice is implemented; the goal is demonstrating the layering and
patterns, not shipping a product.

## Decisions

1. **Three-module Gradle layout.** `Services.Common` (shared DDD kernel) +
   `Customer.App` (domain/application) + `Customer.Api` (Spring host).
   Rationale: mirrors the classic DDD service decomposition; other slices
   (Product, Identity) would follow the same template.
2. **Framework-light domain/app modules.** Domain and use cases are plain
   Java; Spring lives only in `Customer.Api`. Rationale: keeps the domain
   testable and framework-free — the DDD point.
3. **Command + use-case + domain-event vocabulary.** `ICommand`,
   `ICommandHandler`, `IEvent`/`DomainEvent`/`IEventHandler` in Common;
   `RegisterCustomer` command → `RegisterCustomerUseCase` → `CustomerCreated`
   event via `IPublisherServices`. Rationale: demonstrates CQRS-ish flow
   without a bus.
4. **In-memory publisher for events.** `InMemoryPublisherServices`
   substitutes for a message broker. Rationale: no infrastructure for a
   learning project.
5. **Spock for App tests, JUnit for Api.** Groovy specs under
   `src/test/groovy` in `Customer.App`. Rationale: course stack.
6. **Frozen toolchain: Gradle 5.6.1 + Spring Boot 2.1.9 + JDK ≤11.**
   Period-accurate, EOL — accepted; modernisation is a separate project.

## Consequences

- `Customer.Api` exposes nothing yet — REST endpoints are future work;
  the "microservice" is currently a library + empty shell.
- No persistence layer exists — repositories aren't implemented.
- Package roots are inconsistent across modules (course legacy) —
  documented, not normalised.
