# Changelog

## [Unreleased]

- Request-ID support: `RequestOptions.withRequestId(...)` accepted as an optional trailing argument on the inference surfaces (chat `create`/`stream`, responses, embeddings, compare, images, audio `transcribe`); sent as `X-Request-Id` on JSON, SSE, and multipart requests. Invalid IDs (not matching `^[A-Za-z0-9._:-]{1,64}$`) throw `IllegalArgumentException` client-side.
- Top-level response types extend the new `ApiResponse` base class and expose `getRequestId()`, populated from the response `X-Request-Id` header (excluded from Jackson serialization). Stream parsers (`SseParser`, `JsonSseParser`) also expose `getRequestId()`.

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
