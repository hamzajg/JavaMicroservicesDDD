# Gates: order-rest-api

> Preconditions for phase transitions. Each `## G<n> — <title>` block is a
> gate. `ship_loop` refuses to advance past a phase while a blocking gate is
> pending. Definitions live here; status and evidence live in `LOOP.md`.

## G1 — Cross-service call decision recorded
- Before: plan
- Type: decision
- Owner: agent
- Resolver: agent
- Blocking: yes
- Check: file:specs/order-rest-api/SPEC.md
- Why: Order is the first feature spanning bounded contexts; the
  synchronous-REST vs events decision must be explicit before waves.

## G2 — Live four-service demo possible
- Before: validate
- Type: external
- Owner: agent
- Resolver: agent
- Blocking: no
- Check: manual
- Why: Validation is stronger with all four services running; advisory —
  MockMvc evidence stands alone if ports are busy.
