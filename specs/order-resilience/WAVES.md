# Waves — order-resilience

Each wave IS an iteration: a time-boxed, independently shippable
increment — one branch, one PR, green tests, demoable outcome.
Do not start wave N+1 until wave N merges.

## Wave 1 — iteration 1

Scenarios proven by this wave:
- Upstream timeout returns a clean error
- Upstream connection failure returns a clean error
- Upstream server error returns a clean error
- Unknown references still return 404

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US1 | As an API consumer, I want order calls bounded by timeouts with clean upstream errors, so that a slow or down catalog never hangs or confuses a checkout | M | — | RestTemplate becomes an injected, timeout-configured bean; upstream failures map to 502/504. |

## Wave 2 — iteration 2

Scenarios proven by this wave:
- Upstream response contract is pinned

| Item | Title | Size | Deps | Notes |
|------|-------|------|------|-------|
| US2 | As a maintainer, I want a contract test pinning the Customer/Product response fields RemotePorts parses, so that upstream changes break loudly at test time instead of silently at runtime | S | US1 | Asserts only the fields RemotePorts reads (customer id; product id/sku/name/price). |
