# Design: `isValorized` filter on TraceFilter

Issue: #1975 — "[BE] ajout d'un endpoint sur le trace controller qui renvoie les traces valorisées"

## Context

`POST /me/traces/view` already filters traces via the `TraceFilter` record
(`isAssociated`, `fileTypes`, `skillIds`, `statuses`), passed as the request
body. `TraceEntity` / `Trace` already has a persisted `boolean valorized`
field (used by `updateTrace`), but there's no way to filter the trace list by
it. This adds that capability, following the exact same wiring as the
existing `isAssociated` filter.

The issue text says "queryParam", but every existing filter on this endpoint
travels through the `TraceFilter` request body, not the URL query string.
Confirmed with the user: `isValorized` will be a new field on `TraceFilter`,
not a separate `@RequestParam`.

## Semantics

`isValorized` is a nullable `Boolean`, symmetric with `isAssociated`:

- `null` → no filtering (unchanged behavior).
- `true` → only traces where `valorized = true`.
- `false` → only traces where `valorized = false`.

## Changes

1. **`TraceFilter`** (`trace/domain/filter/TraceFilter.java`)
   Add `Boolean isValorized` as the last field of the record (after
   `statuses`), to minimize disruption to existing positional call sites.
   `toMap()` always puts `ETraceFilterKey.IS_VALORIZED` in the map, even when
   null — mirrors how `IS_ASSOCIATED` is handled today.

2. **`ETraceFilterKey`** (`trace/domain/filter/ETraceFilterKey.java`)
   Add `IS_VALORIZED` constant.

3. **`TraceSpecification`**
   (`trace/infrastructure/adapter/specification/TraceSpecification.java`)
   Add:
   ```java
   public static Specification<TraceEntity> valorized(boolean value) {
     return (root, query, cb) -> cb.equal(root.get("valorized"), value);
   }
   ```
   A single method suffices (unlike `associated()`/`unassociated()`, which
   need a subquery + negation) because `valorized` is a plain boolean column.

4. **`TraceFilterSpecificationBuilder`**
   (`trace/infrastructure/adapter/specification/TraceFilterSpecificationBuilder.java`)
   Add a `case IS_VALORIZED` branch: returns `null` when the value is null,
   otherwise `TraceSpecification.valorized((Boolean) value)`.

5. **`TraceController`** — no signature change. `isValorized` arrives inside
   the existing `@RequestBody TraceFilter traceFilter` on `tracesView`.

6. **Call-site updates** — every existing `new TraceFilter(...)` call
   (in `DeclaredActivityServiceImpl` and test files) gets an extra trailing
   `null` argument for the new field.

## Testing

- **`TraceFilterTest`** (unit): update the existing
  `shouldAlwaysContainIsAssociated_evenWhenNull` size assertion (1 → 2, since
  `IS_VALORIZED` is now always present too). Add cases asserting the map
  contains `IS_VALORIZED` for null/true/false values.
- **`TraceSpecificationIT`**: add a DB-backed test persisting a valorized and
  a non-valorized trace, asserting `TraceFilterSpecificationBuilder` /
  `TraceSpecification.valorized(...)` filters correctly for `true` and
  `false`, plus a null-branch coverage test consistent with
  `shouldCoverFilterBuilderQueryNullBranches`.
- **`TraceControllerIT`**: add an integration test hitting
  `POST /me/traces/view` with `isValorized: true`, `false`, and omitted,
  asserting the returned trace set matches expectations.

## Out of scope

- No new REST endpoint or `@RequestParam` — reusing the existing `/view`
  endpoint and its `TraceFilter` body, per user confirmation.
- No changes to `TraceEntity`/`Trace`/`updateTrace` — the `valorized` field
  and its mutation already exist.
