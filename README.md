# meshapi-java-sdk

Java SDK for the MeshAPI AI model gateway.

## Requirements

- Java 17+
- Maven 3.6+

## Installation

Add to `pom.xml`:

```xml
<dependency>
  <groupId>com.meshapi.sdk</groupId>
  <artifactId>meshapi-java-sdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Quick Start

```java
MeshAPI client = MeshAPI.builder()
    .baseUrl("https://api.meshapi.ai")
    .token("rsk_...")
    .build();

ChatCompletionResponse resp = client.chat().completions().create(
    ChatCompletionRequest.builder()
        .model("openai/gpt-4o-mini")
        .addMessage(ChatMessage.user("What is 2+2?"))
        .build()
);
System.out.println(resp.choices.get(0).message.content);
```

## Chat completions

```java
// Non-streaming
ChatCompletionResponse resp = client.chat().completions().create(
    ChatCompletionRequest.builder()
        .model("openai/gpt-4o-mini")
        .addMessage(ChatMessage.user("Hello!"))
        .build()
);

// Streaming
Iterator<ChatCompletionChunk> it = client.chat().completions().stream(params);
while (it.hasNext()) {
    ChatCompletionChunk chunk = it.next();
    if (!chunk.choices.isEmpty() && chunk.choices.get(0).delta != null) {
        System.out.print(chunk.choices.get(0).delta.content);
    }
}
```

## Responses API (reasoning models)

```java
ResponsesResponse reason = client.responses().create(
    ResponsesRequest.builder()
        .model("openai/o3-mini")
        .input("Why is the sky blue?")
        .build()
);
```

## Embeddings

```java
EmbeddingsResponse emb = client.embeddings().create(
    EmbeddingsRequest.builder()
        .model("openai/text-embedding-3-small")
        .input(List.of("The quick brown fox"))
        .build()
);
```

## Image generation

```java
ImageGenerationResponse img = client.images().generate(
    ImageGenerationRequest.builder()
        .model("openai/gpt-image-1")
        .prompt("A watercolor of a fox in a snowy forest")
        .n(1).size("1024x1024").quality("high").outputFormat("webp")
        .build()
);

// Streaming
Iterator<ImageGenerationChunk> stream = client.images().stream(params);
while (stream.hasNext()) { ... }
```

## Compare (multi-model)

```java
Iterator<CompareStreamEvent> compare = client.compare().stream(
    CompareRequest.builder()
        .addModel("openai/gpt-4o-mini")
        .addModel("anthropic/claude-haiku-4.5")
        .addMessage(ChatMessage.user("Hi!"))
        .build()
);
```

## Batches

Batch jobs accept inline requests — no separate file upload step required.

```java
CreateBatchRequest req = new CreateBatchRequest();
req.requests = List.of(
    buildItem("req-1", "openai/gpt-5-nano", "Say hello."),
    buildItem("req-2", "openai/gpt-5-nano", "Say goodbye.")
);
req.metadata = Map.of("job", "my-batch");

BatchObject batch = client.batches().create(req);

// Poll
BatchObject got = client.batches().get(batch.id);
System.out.println(got.status);

// Cancel
client.batches().cancel(batch.id);
```

## RAG (Retrieval-Augmented Generation)

Upload files, embed them, and run vector search.

```java
// 1. Initialise upload — get a signed URL
InitUploadResponse upload = client.rag().initUpload(
    InitUploadRequest.builder()
        .fileName("handbook.pdf")
        .mimeType("application/pdf")
        .build());

// 2a. PUT file bytes to the signed URL yourself…
java.net.http.HttpClient jdkHttp = java.net.http.HttpClient.newHttpClient();
jdkHttp.send(
    java.net.http.HttpRequest.newBuilder()
        .uri(URI.create(upload.signedUrl))
        .header("Content-Type", "application/pdf")
        .PUT(java.net.http.HttpRequest.BodyPublishers.ofByteArray(fileBytes))
        .build(),
    java.net.http.HttpResponse.BodyHandlers.discarding());

// 2b. …or use the convenience wrapper that does both steps:
upload = client.rag().uploadFile(
    "handbook.pdf", "application/pdf", fileBytes, null, null);

// 3. Trigger embedding
client.rag().embed(
    BulkEmbedRequest.builder()
        .fileIds(List.of(upload.fileId))
        .build());

// 4. Poll until ready
while (true) {
    RagFileStatus s = client.rag().get(upload.fileId);
    if ("ready".equals(s.embeddingStatus)) break;
    Thread.sleep(3_000);
}

// 5. Search
SearchResponse results = client.rag().search(
    SearchRequest.builder()
        .query("onboarding process")
        .topK(5)
        .build());
results.results.forEach(r ->
    System.out.printf("%.4f  %s%n", r.score, r.text));

// List files
RagFileListResponse list = client.rag().list(50, null);
```

## Realtime (Speech-to-Speech WebSocket)

```java
import com.meshapi.sdk.resources.RealtimeSession;
import com.meshapi.sdk.types.realtime.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

RealtimeSession session = client.realtime().connect(
    "openai/gpt-4o-realtime-preview",
    new RealtimeListener() {
        @Override
        public void onOpen(RealtimeSession s) {
            System.out.println("session open");
        }

        @Override
        public void onMessage(RealtimeSession s, RealtimeMessage m) {
            if (m.isAudio()) {
                processAudio(m.getAudio());          // binary audio frame
            } else {
                System.out.println(m.getEvent().get("type")); // "session.created", …
            }
        }

        @Override
        public void onError(RealtimeSession s, RealtimeError e) {
            System.err.println(e.getCode());         // "insufficient_quota", …
        }

        @Override
        public void onClose(RealtimeSession s, int code, String reason) {
            System.out.println("closed: " + code);
        }
    }
).get(15, TimeUnit.SECONDS);

try (session) {
    session.send(Map.of(
        "type", "session.update",
        "session", Map.of("instructions", "You are a helpful assistant.")
    )).join();
    session.sendAudio(pcmBytes).join();
    Thread.sleep(5_000);  // exchange frames …
}
```

Built on JDK 17's built-in `java.net.http.WebSocket` — no additional dependencies required.

## Models

```java
List<ModelInfo> models = client.models().list(null);
List<ModelInfo> free   = client.models().free();
```

## Templates

```java
TemplateSummary tmpl = client.templates().create(
    CreateTemplateRequest.builder().name("my-tpl").system("You are helpful.").build());
client.templates().delete(tmpl.id);
```

## Error handling

```java
try {
    resp = client.chat().completions().create(params);
} catch (MeshAPIError e) {
    System.out.println(e.getStatus());            // HTTP status
    System.out.println(e.getErrorCode());         // "unauthorized", "rate_limit_exceeded", …
    System.out.println(e.getRequestId());         // req_<ULID>
    System.out.println(e.getRetryAfterSeconds()); // non-null on 429
}
```

## Retry / backoff

Retries on 429/502/503/504 with exponential backoff (default 3 retries, 500 ms base, 30 s max, ±20% jitter).

```java
MeshAPI client = MeshAPI.builder()
    .baseUrl("...")
    .token("...")
    .maxRetries(5)    // 0 to disable
    .timeoutMs(30_000)
    .build();
```

**Streams do not retry.** On failure, `Iterator.next()` throws `MeshAPIError`.

## Running tests

```bash
# Unit + contract tests (no server needed)
mvn test

# Live tests (requires a running backend)
mvn install -DskipTests
cd livetests
MESHAPI_TOKEN=rsk_... mvn test
```

## Versioning

```java
System.out.println(MeshAPI.VERSION); // "0.1.0"
```
