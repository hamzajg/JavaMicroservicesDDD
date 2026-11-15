# Spec: order-auth

> Spec-driven development artifact. This file is the contract: implementation
> is done when every acceptance criterion maps to a scenario in
> `EXAMPLES.feature` and every scenario has a passing check.

## Problem

`Order.Api` (:8083) exposes order placement, listing, and cancellation to
anyone who can reach the port — no authentication at all. Identity.Api
(:8082) already issues opaque bearer tokens (`POST /identity/login`) and
exposes `GET /identity/me` for introspection, but nothing consumes it.
Orders are the transactional, customer-data-bearing surface of the
system; they should not be world-writable.

## Users

Registered shoppers (hold a token from Identity login); operators who
need unauthenticated traffic rejected before it reaches order logic.

## Scope (in)

- A servlet filter guarding **all** `/orders/**` endpoints on Order.Api.
- Token validation by calling Identity.Api `GET /identity/me` with the
  caller's `Authorization: Bearer <token>` — sync HTTP, reusing the
  timeout-configured `RestTemplate` bean from `order-resilience`.
- Outcomes: missing/malformed/invalid token → `401`; Identity
  unreachable or timing out → `503` (fail closed, distinct from 401);
  valid token → request proceeds unchanged.
- Identity.Api itself unchanged (`/identity/me` already introspects).

## Scope (out)

- Auth on Customer/Product/Identity APIs (separate feature — same
  pattern applies).
- Roles/scopes/authorization beyond "valid token" (no RBAC).
- Token format changes (JWT, refresh tokens) — opaque tokens stay.
- Persisting tokens or sessions anywhere new.

## Acceptance criteria

| # | Criterion | Scenario(s) |
|---|-----------|-------------|
| 1 | Requests to /orders without a Bearer token are rejected 401 | Missing token is rejected |
| 2 | Requests with an invalid/expired token are rejected 401 | Invalid token is rejected |
| 3 | A valid Identity token lets the request through to order logic | Valid token reaches order logic |
| 4 | Identity being unreachable returns 503, not a hang or 500 | Identity outage fails closed |
| 5 | The introspection call itself is bounded by timeouts | Introspection call is bounded |

## Decisions

- **Filter, not interceptor**: a `javax.servlet.Filter` registered for
  `/orders/*` keeps auth outside the controller and out of `Order.App`
  (which stays framework-light).
- **Introspection over `/identity/me`**: Identity already owns token
  state; Order.Api never sees credentials, only a yes/no. This is the
  same hexagonal-port instinct as `order-rest-api` — but implemented as
  an API-layer adapter (`TokenIntrospector`), not a domain port, because
  auth is an inbound concern.
- **503 for Identity outage**: distinct from 401 so clients can retry;
  consistent with fail-closed auth without leaking which check failed.
- **Opaque tokens unchanged**: no JWT migration — the seam
  (`AuthenticateTokenUseCase`) remains the documented upgrade point.
