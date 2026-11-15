# Spec: order-resilience

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

`Order.Api` calls Customer (:8080) and Product (:8081) through
`RemotePorts` using a bare `new RestTemplate()` — no connect/read
timeouts, so a stalled upstream hangs the request indefinitely, and any
upstream failure (timeout, connection refused, 5xx) escapes as an
unhandled 500. There is also no contract test pinning the Customer/Product
JSON shapes that `RemotePorts` parses, so an upstream response change
breaks ordering silently at runtime. Surfaced by the ADR 0002 decision
review (`docs/adr/0002-order-cross-service-calls-review.md`, smallest
next step).

## Users

API consumers placing orders; operators who need order placement to fail
fast and legibly when an upstream is degraded.

## Scope (in)

- Configured connect/read timeouts on the `RestTemplate` used by
  `RemotePorts` (property-driven with sane defaults).
- A clean error contract for upstream failures: timeout, connection
  refused, and upstream 5xx map to `502 Bad Gateway` (or `504` for
  timeouts) instead of hanging or an opaque 500.
- A contract test that pins the Customer/Product response fields
  `RemotePorts` depends on.
- Preserve existing semantics: unknown customer/product → 404,
  validation failures → 400.

## Scope (out)

- Retries, circuit breakers, bulkheads (revisit trigger in ADR 0002).
- Async/event-driven ordering, service discovery, auth between services.
- Changing the port interfaces or the Order domain model.

## Acceptance criteria

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | Upstream calls are bounded by configured connect/read timeouts | @w1 |
| 2 | An upstream timeout or connection failure returns a clean 5xx, never a hang or opaque 500 | Upstream timeout returns a clean error, Upstream connection failure returns a clean error |
| 3 | An upstream 5xx response returns a clean 502 | Upstream server error returns a clean error |
| 4 | Unknown customer/product references still return 404 | Unknown references still return 404 |
| 5 | The Customer/Product response contract `RemotePorts` parses is pinned by a test | Upstream response contract is pinned |

## Decisions

- `RestTemplate` becomes a configured Spring bean (timeouts via
  `SimpleClientHttpRequestFactory` or `ClientHttpRequestFactorySettings`)
  injected into `RemotePorts` — keeps `Order.App` ports unchanged and
  makes the adapter testable.
- Upstream failures map through a `@RestControllerAdvice` (or equivalent)
  to `502 Bad Gateway`; `ResourceAccessException` (timeout/connect)
  may map to `504 Gateway Timeout` where the cause is a read timeout.
- Contract test asserts only the fields `RemotePorts` actually reads
  (customer `id`, product `id`/`sku`/`name`/`price`) — not full payloads.
