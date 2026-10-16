# Decisions

## Contract drift gate

- `contract-baseline.json` allows the 559 spec members the SDK does not cover yet. Each row is a known gap, not an approval; the gate fails on anything new, and stale rows must be deleted.
- Model fields come from reflection over compiled `types` classes. Request params come from a regex scan of `resources/*.java`, because reflection cannot see request paths.
- Path params are named by the Java identifier, so they differ from spec names and sit in the baseline. Free-form `Map` query params are not reported.
- Known latent: the query-key scan covers the whole method body, not just its own http call; same-simple-name nested classes are unioned; the spec-version label is not tied to the snapshot content (no hash).
