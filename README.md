# meshapi-java-sdk

Java SDK for the MeshAPI AI model gateway.

## Requirements

- Java 17+
- Maven 3.6+

## Installation

Add to `pom.xml`:
```xml
<dependency>
  <groupId>com.meshapi</groupId>
  <artifactId>meshapi-java-sdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Quick Start

```java
MeshAPI client = MeshAPI.builder()
    .baseUrl("http://localhost:8000")
    .token("rsk_...")
    .build();

// Non-streaming
ChatCompletionResponse resp = client.chat().completions().create(
    ChatCompletionRequest.builder()
        .model("openai/gpt-4o-mini")
        .addMessage(ChatMessage.user("What is 2+2?"))
        .build()
);
System.out.println(resp.choices.get(0).message.content); // "4"

// Streaming
Iterator<ChatCompletionChunk> it = client.chat().completions().stream(params);
while (it.hasNext()) {
    ChatCompletionChunk chunk = it.next();
    if (!chunk.choices.isEmpty() && chunk.choices.get(0).delta != null) {
        System.out.print(chunk.choices.get(0).delta.content);
    }
}

// Models
List<ModelInfo> models = client.models().list(null);
List<ModelInfo> free   = client.models().free();
List<ModelInfo> paid   = client.models().paid();

// Templates
TemplateSummary tmpl = client.templates().create(
    CreateTemplateRequest.builder().name("my-tpl").system("You are helpful.").build()
);
client.templates().delete(tmpl.id);
```

## Error Handling

```java
try {
    resp = client.chat().completions().create(params);
} catch (MeshAPIApiError e) {
    System.out.println(e.getStatus());           // HTTP status
    System.out.println(e.getErrorCode());        // "unauthorized", "rate_limit_exceeded", etc.
    System.out.println(e.getRequestId());        // req_<ULID>
    System.out.println(e.getRetryAfterSeconds()); // non-null on 429
}
```

## Retry / Backoff

Retries on 429/502/503/504 with exponential backoff (default 3 retries, 500 ms base, 30 s max, ±20% jitter).

```java
MeshAPI client = MeshAPI.builder()
    .baseUrl("...")
    .token("...")
    .maxRetries(5)   // 0 to disable
    .timeoutMs(30_000)
    .build();
```

## Streaming Failure Recovery

**Streams do not retry.** On failure, `Iterator.next()` throws `MeshAPIApiError`.

```java
try {
    while (it.hasNext()) { process(it.next()); }
} catch (MeshAPIApiError e) {
    if ("stream_interrupted".equals(e.getErrorCode())) {
        // restart a new stream request
    }
}
```

## Running Tests

```bash
# Unit + contract tests (no server needed)
mvn test

# Integration tests (requires localhost:8000)
MESHAPI_BASE_URL=http://localhost:8000 \
MESHAPI_TOKEN=rsk_... \
mvn test -Pintegration

# Build JAR (test classes excluded automatically)
mvn package -DskipTests
```

## Versioning

```java
System.out.println(MeshAPI.VERSION); // "0.1.0"
```
