# Architecture — JavaMicroservicesDDD

Current-state map. Descriptive, not aspirational; see `docs/adr/` for why.

## Shape

```
JavaMicroservicesDDD/   (Gradle multi-project, 3 modules)
├── settings.gradle      maps :Services.Common/:Customer.App/:Customer.Api → src/*
├── build.gradle         shared config
└── src/
    ├── Services.Common/   DDD kernel (Java)
    │   ├── Commands/      ICommand, ICommandHandler
    │   └── Domain/        Entity, AggregateRoot,
    │                      Events/{IEvent, DomainEvent, IEventHandler}
    ├── Customer.App/      customer slice (Java + Groovy/Spock tests)
    │   ├── Domain/        Customer aggregate + FullName/Email/Address/
    │   │                  PhoneNumber value objects
    │   ├── Events/        CustomerCreated domain event
    │   ├── UseCases/      IRegisterCustomerUseCase → RegisterCustomerUseCase
    │   │                  (impl: RegisterCustomer command → RegisterCustomerUseCase)
    │   └── Services/      IPublisherServices + InMemoryPublisherServices
    └── Customer.Api/      Spring Boot 2.1.9 — Application.java only,
                           web+webflux deps, NO controllers yet
```

## Layers

| Module | Layer | Contents |
|---|---|---|
| `Services.Common` | shared kernel | command/event handler interfaces, Entity, AggregateRoot, DomainEvent |
| `Customer.App` | domain + application | aggregate, VOs, domain event, use-case interface+impl+command, in-memory publisher |
| `Customer.Api` | host | Spring Boot shell — endpoints not yet implemented |

## State & events

- Persistence: none — no repository implementations exist yet.
- Events: `CustomerCreated` published via `IPublisherServices`
  (in-memory impl) — the event-driven pattern is scaffolded, not wired to
  a broker.

## Frontend ↔ backend

No frontend; the API layer is an empty Spring Boot shell. First real
endpoint should be a `@RestController` in `Customer.Api` delegating to
`Customer.App`'s use cases.

## Toolchain

Gradle 5.6.1 wrapper ⇒ **JDK 8/11 only** (no Java 17). CI: JDK 11,
`sh gradlew build --no-daemon`.
