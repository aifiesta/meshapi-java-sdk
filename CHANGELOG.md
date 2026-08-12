# Changelog

## [Unreleased]

### API version

- **This release targets MeshAPI version `2026-08`.** Every request now sends
  `X-Mesh-Version: 2026-08`, exposed as `MeshAPI.API_VERSION` — distinct from
  `MeshAPI.VERSION`, which identifies the SDK build.
- Pin a different one with `MeshAPI.builder().apiVersion("2026-09")`, or pass `null`
  to send no header and take the gateway's baseline (the previous behaviour).
- Why it matters: an unpinned client is served whatever the gateway defaults to, so it
  never states which response shape it can parse. Pinning means a future version that
  changes a shape cannot change it underneath this release.

### Changed

- `internal.HttpClient` builds every request through one private `request(url)` helper.
  The headers common to all calls were previously repeated at **eight** separate
  `HttpRequest.newBuilder()` sites, so adding one meant editing all eight and a miss
  was silent. Callers still set their own `Content-Type` / `Accept`, which genuinely
  differ per call shape.

### Fixed

- `ModelInfo.ModelPricing` now declares `inputUsdPerUnit` / `outputUsdPerUnit`. With
  `@JsonIgnoreProperties(ignoreUnknown = true)` these were being **discarded**: for
  models that are not token-priced — per-second video, per-image, per-1k-chars — the
  per-1M fields are null by design, which left the SDK reporting a priced model as
  having no price.
- `promptUsdPer1k` / `completionUsdPer1k` are documented as retired: the gateway stopped
  returning them in `v1.0.135`, so they are always null. They remain declared for
  backwards compatibility. Their `// Required` comment was wrong.

## [0.1.0] — Initial release

- `MeshAPI` with Builder pattern; `chat()`, `models()`, `templates()` resources
- Chat completions: `create` (non-streaming) and `stream` (Iterator-based)
- Models: `list(Boolean free)`, `free()`, `paid()`
- Templates: `create`, `list`, `get`, `update`, `delete`
- `MeshAPIApiError` extends `RuntimeException` with `status`, `errorCode`, `requestId`, `details`, `retryAfterSeconds`
- Retry with exponential backoff (default 3 retries, codes 429/502/503/504)
- `SseParser` with blank-line frame delimiter, [DONE] sentinel, mid-stream error detection
- Streaming fail-fast: no automatic reconnect
- `X-MeshAPI-SDK: java/0.1.0` header on every request
