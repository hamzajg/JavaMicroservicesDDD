# JavaMicroservicesDDD

A small multi-module Spring Boot project exploring microservices with
Domain-Driven Design in Java.

## Modules

| Module | Role |
|---|---|
| `Services.Common` | Shared library code used by the services |
| `Customer.App` | Customer application/domain layer |
| `Customer.Api` | Customer REST API (Spring Boot, WebFlux) |

## Tech stack

- Java 8 (`Customer.Api`) / Java library modules
- Spring Boot 2.1.9 with Spring Web and WebFlux
- Spock + JUnit 4 for tests
- Gradle 5.6.1 (wrapper included)

## Project layout

```
src/
  Services.Common/   # shared library
  Customer.App/      # application layer
  Customer.Api/      # Spring Boot API
gradlew, gradlew.bat
settings.gradle
```

## Building and running

Use JDK 8 or 11 (the build predates Java 17 support in Gradle 5.6.1):

```bash
./gradlew build          # compile + test all modules
./gradlew :Customer.Api:bootRun
```

The API starts on Spring Boot's default port (8080) unless configured otherwise.

## Tests

```bash
./gradlew test
```

Each module keeps its tests under `src/test`; the library modules use Spock
specifications and JUnit 4.

## Status

Learning/reference project. The customer slice is implemented; the
`Product` and `Identity` slices of a typical e-shop are not present here.
