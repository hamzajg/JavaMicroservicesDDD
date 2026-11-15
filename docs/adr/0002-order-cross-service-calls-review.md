# Decision review brief — ADR 0002 — Cross-service ordering via synchronous REST ports

- Source: `docs/adr/0001-order-cross-service-calls.md`
- Generated: 2026-11-15T19:55:48Z
- Detected type: architecture, api
- Readiness: **CHALLENGE** (6/6 record elements)
- This brief is input to judgment, not a verdict. The agent fills the scorecard after reading the code.

**Reversibility:** stated

## Evidence signals (heuristic — verify before acting)

- `src/Order.Api/src/main/java/com/hamzajg/order/api/RemotePorts.java` — no test file references this code (heuristic)
- `specs/order-rest-api/SPEC.md` — no source files found to inspect

Inspected: `src/Order.App/src/main/java/Order/App/Ports`, `src/Order.Api/src/main/java/com/hamzajg/order/api/RemotePorts.java`, `specs/order-rest-api/SPEC.md`

## Design moves to test

- Can this be a module inside the current deployable before it becomes a service?
- Which dependency can be inverted so the core owns the interface?
- What is the smallest reversible step that tests the boundary?
- Is this a public contract or an implementation detail? Who depends on it?
- How will it version, and what is the backward-compatibility promise?
- Are errors part of the contract, or an afterthought?
- Invert a dependency so the unstable thing depends on the stable thing.
- Make an implicit boundary or invariant explicit before adding mechanism.
- Shrink the decision: can half of it ship independently and reversibly?

## Challenge questions

### 1. Problem & forces
- What user/business outcome does this serve, and what breaks if we do nothing?
- Which quality attributes actually drive it (correctness, latency, cost, operability, compliance, time-to-market)? Rank them.
- Which constraints are fixed (budget, deadline, team, contracts) and which are assumed?
- Who owns this decision, and who must live with the consequences?
**Good looks like:** The dominant force is named, and "do nothing" is treated as a real option.

### 2. Alternatives & tradeoffs
- What are at least two credible alternatives, including the simplest thing that could work?
- For each: what does it make easy, what does it make hard, and what does it cost to reverse?
- Is this a one-way or two-way door? If one-way, what evidence justifies it?
- What would have to be true for the chosen option to be wrong?
**Good looks like:** A tradeoff matrix names winners and losers; reversibility is explicit; no strawman options.

### 3. Domain fit (DDD)
- Which bounded context owns this concept, and is the boundary drawn around language or deployment?
- Which terms are overloaded across contexts, and where is the translation (anti-corruption layer)?
- What are the aggregates, their invariants, and the transaction boundary? Does one transaction span aggregates?
- Are domain events the right integration mechanism, or is this a synchronous dependency?
- Does the model carry behavior, or is it an anemic data bag?
**Good looks like:** Ubiquitous language is explicit, invariants are protected at the boundary, external models are translated.

### 4. Architecture & boundaries
- Which direction do dependencies point? Can the core be tested without the framework, DB, or network?
- What is the smallest boundary that isolates this change (module, port, package)?
- What happens when each external dependency is slow, down, or returns garbage?
- Where does data ownership live, and is there a second writer?
- What is the operational story: config, migrations, rollout, rollback, observability?
- Who are the consumers, and what is the deprecation policy?
**Good looks like:** Policy depends on nothing; mechanisms are adapters; failure and migration paths are named.

### 5. Testable design
- Where are the seams? Can time, randomness, IO, and external calls be injected?
- Which behaviors get fast unit tests, which need integration, which need contract tests?
- Does each acceptance criterion map to an observable check, or only to implementation details?
- Are the tests deterministic and independent (no shared state, no sleeps)?
- What is the failure signal when this decision turns out wrong in production?
**Good looks like:** Behavior-first tests, deterministic seams, scenario-to-test traceability, a named failure signal.

### 6. Craftsmanship
- Is this the smallest reversible step, or a big-bang change?
- Are names from the domain, or from the framework/table?
- Is the error model explicit (typed failures vs exceptions vs silent nulls)?
- Is there speculative abstraction (interfaces with one implementation, config nobody sets)?
- Is the migration path documented and rollback-able?
**Good looks like:** Small steps, intention-revealing names, explicit errors, no speculative generality, documented migration.

### 7. Decision record
- Are status, owner, and date recorded?
- Are consequences (positive and negative) written down?
- Is there a revisit trigger (metric, date, event)?
- Are links to evidence, code, and related ADRs present?
**Good looks like:** A future reader can tell why, what was traded, and when to revisit.

## Scorecard (agent fills; 1 = poor, 5 = strong, evidence required)

| Lens | Score | Evidence (file:line, test, benchmark) | Action |
|------|-------|----------------------------------------|--------|
| Problem & forces | 4 | ADR Context names MVP/no-broker constraints; price correctness is the dominant force | Name the dominant quality attribute explicitly |
| Alternatives & tradeoffs | 4 | ADR lists 4 options incl. rejected client-supplied prices; event-sourcing cost named | Add an explicit winner/loser matrix per option |
| Domain fit (DDD) | 4 | Ports at Order/App/Ports/*.java; ids-only boundary = implicit ACL; Order aggregate owns total computation | Document the price-snapshot invariant in the aggregate |
| Architecture & boundaries | 3 | RemotePorts.java uses bare RestTemplate — no timeouts, retries, or circuit breaking; temporal coupling acknowledged but unmitigated | Add connect/read timeouts; consider resilience4j later |
| Testable design | 4 | Ports mocked in OrderControllerTest; OrderUseCaseTests.groovy carries // covers scenario tags; live 3-service walkthrough in VALIDATION.md | Add a contract test pinning Customer/Product response shapes |
| Craftsmanship | 4 | Two real ports, no speculative abstraction; errors surface as 404/409 via use-case exceptions | — |
| Decision record | 4 | Status/context/alternatives/consequences/evidence present; revisit trigger added after this review found it missing | Record an owner |

## Verdict (agent fills)

```
VERDICT: ACCEPT
STEELMAN: For an MVP with no broker and one team, synchronous REST behind
hexagonal ports is the smallest correct step — it validates real customer
and catalog state, keeps Order.App framework-free, and leaves the
port seam ready for an event-driven replacement without domain churn.
SMALLEST NEXT STEP: add connect/read timeouts to RemotePorts and one
contract test asserting the Customer/Product response shapes Order
depends on.
REVISIT TRIGGER: a message broker enters the platform, order-placement
latency or upstream availability becomes a measured problem, or a
second context needs order data asynchronously (per ADR §Revisit).
```

Run the review with `/challenge-decision` and the `decision-challenger`
subagent. It is advisory: it never blocks a gate or a merge.
