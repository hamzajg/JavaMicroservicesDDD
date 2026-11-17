# Validation — order-resilience

## Traceability

| AC | Criterion | Scenarios | Wave(s) | Test evidence |
|----|-----------|-----------|---------|---------------|
| 1 | Upstream calls are bounded by configured connect/read… | Unknown references still return 404, Upstream connection failure returns a clean error, Upstream server error returns a clean error, Upstream timeout returns a clean error | 1 | OrderControllerTest.java, RemotePortsTest.java |
| 2 | An upstream timeout or connection failure returns a clean… | Unknown references still return 404, Upstream connection failure returns a clean error, Upstream server error returns a clean error, Upstream timeout returns a clean error | 1 | OrderControllerTest.java, RemotePortsTest.java |
| 3 | An upstream 5xx response returns a clean 502 | Unknown references still return 404, Upstream connection failure returns a clean error, Upstream server error returns a clean error, Upstream timeout returns a clean error | 1 | OrderControllerTest.java, RemotePortsTest.java |
| 4 | Unknown customer/product references still return 404 | Unknown references still return 404, Upstream connection failure returns a clean error, Upstream server error returns a clean error, Upstream timeout returns a clean error | 1 | OrderControllerTest.java, RemotePortsTest.java |
| 5 | The Customer/Product response contract `RemotePorts` parses… | Upstream response contract is pinned | 2 | UpstreamContractTest.java |

## Scenario coverage

- Scenarios: 5 total, 5 with detected test evidence, 0 without
- ✓ `Upstream timeout returns a clean error` (@w1)
- ✓ `Upstream connection failure returns a clean error` (@w1)
- ✓ `Upstream server error returns a clean error` (@w1)
- ✓ `Unknown references still return 404` (@w1)
- ✓ `Upstream response contract is pinned` (@w2)

## Demo script (manual walkthrough)

1. **Upstream timeout returns a clean error** — run the app, perform the scenario, observe the outcome.

## Gaps

- none — spec is fully traced

Note: 'no evidence' means neither the scenario name nor a
token match was found in test files — verify by hand before
treating it as a real gap. '~' marks weak token-match evidence.

## Outcome follow-up

Optional; this is a post-release learning record, not a finish gate.
- Status: not yet measured
- Owner and review date: (record when known)
- Signal and observed result: (record after release; do not invent)
- Learning or next action: (record after review)