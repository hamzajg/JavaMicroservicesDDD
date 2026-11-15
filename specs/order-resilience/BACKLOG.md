# Backlog: order-resilience

Items are USER STORIES in the parseable format below — keep the heading
shape `## US<n> — <story>` and the field lines intact so `wave_plan` can
parse them. Write the story as: "As a <role>, I want <goal>, so that
<value>" — the title after the dash IS the story.

```
## US<n> — As a <role>, I want <goal>, so that <value>
- Scenarios: @w1, @w2
- Depends: US1, US3        (optional; omit for wave-1 items)
- Size: S|M|L
- Notes: <one line>
```

Order stories by user value; dependencies express technical order only.

## US1 — As an API consumer, I want order calls bounded by timeouts with clean upstream errors, so that a slow or down catalog never hangs or confuses a checkout
- Scenarios: @w1
- Size: M
- Notes: RestTemplate becomes an injected, timeout-configured bean; upstream failures map to 502/504.

## US2 — As a maintainer, I want a contract test pinning the Customer/Product response fields RemotePorts parses, so that upstream changes break loudly at test time instead of silently at runtime
- Scenarios: @w2
- Depends: US1
- Size: S
- Notes: Asserts only the fields RemotePorts reads (customer id; product id/sku/name/price).
