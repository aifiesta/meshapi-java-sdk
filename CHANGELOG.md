# Changelog

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
