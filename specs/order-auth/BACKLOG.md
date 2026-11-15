# Backlog: order-auth

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

## US1 — As a shopper, I want my order calls to require a valid Identity token, so that only registered users can place, view, or cancel orders
- Scenarios: @w1
- Size: M
- Notes: BearerAuthFilter on /orders/* + TokenIntrospector calling Identity /identity/me; 401 invalid, 503 Identity down; introspection reuses timeout-configured RestTemplate.
