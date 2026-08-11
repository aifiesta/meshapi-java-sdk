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

## API version

MeshAPI versions its contract by date. This release targets **`2026-08`** and sends it
as `X-Mesh-Version` on every request:

```java
MeshAPI.API_VERSION; // "2026-08" — the contract this release was built to parse

// Pin a newer version, if you have migrated ahead of this SDK release:
MeshAPI.builder().baseUrl(baseUrl).token(token).apiVersion("2026-09").build();

// Send no header at all and take the gateway's baseline, whatever it becomes:
MeshAPI.builder().baseUrl(baseUrl).token(token).apiVersion(null).build();
```

Never calling `apiVersion(...)` uses this SDK's version; passing `null` is an explicit
opt-out. Note `MeshAPI.API_VERSION` is the API contract while `MeshAPI.VERSION` is the
SDK build — they move independently.

**Why pin.** An unpinned client is served whatever the gateway defaults to, so it never
states which response shape it can parse. That is safe today only because the baseline
is the *oldest* supported version and so never moves on its own. Pinning makes it a
contract instead of a coincidence.

The gateway rejects a version it does not serve with `400 invalid_api_version` rather
than falling back, so a typo cannot leave you believing you are pinned when you are
not. `GET /v1/api-versions` lists what a deployment serves.

Not sent on the realtime WebSocket handshake — the gateway's versioning applies to HTTP
requests only, and realtime negotiates its version separately.

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

## Structured outputs

`parse(request, Type.class)` constrains the model to a JSON schema and decodes
the reply into your type. The schema is derived from the class by reflection
(respecting Jackson's `@JsonProperty`) — define a POJO and `parse` builds the
schema and the typed result.

```java
public class Country {
    @JsonProperty("country") public String country;
    @JsonProperty("capital") public String capital;
    @JsonProperty("population_millions") public double populationMillions;
}

Country country = client.chat().completions().parse(
    ChatCompletionRequest.builder()
        .model("openai/gpt-4o-mini")
        .addMessage(ChatMessage.user("Give me structured facts about France."))
        .build(),
    Country.class
);
System.out.println(country.capital + " " + country.populationMillions); // typed
```

Options via `StructuredParseOptions`: `maxRetries(n)` re-prompts on a decode
failure (default 0, each retry is a billed call); `schema(Map)` overrides the
auto-derived schema; `schemaName(String)` sets the schema label.

```java
client.chat().completions().parse(request, Country.class,
    StructuredParseOptions.create().maxRetries(2));
```

> Jackson does not enforce required fields — a missing field decodes to its
> default. Type mismatches and non-JSON prose are caught.

> **Schema derivation is field-based.** The auto-derived schema reads a POJO's
> declared fields and field-level `@JsonProperty` / `@JsonIgnore` only — it does
> not inspect getters, setters, or constructor (`@JsonCreator`) parameters. If a
> type exposes its Jackson properties through accessors or a constructor rather
> than fields, pass an explicit schema with `StructuredParseOptions.schema(...)`.
> `parse` also leaves the request you pass in unchanged: the schema and any retry
> turns are applied to an internal copy, so the same request object can be reused.

### When the model doesn't support structured output

If decoding fails after any retries, `parse` throws `StructuredOutputError` (a
`MeshAPIError` subclass; the underlying Jackson error is on `getCause()`). When
the model returns plain text instead of JSON — usually because it doesn't support
`response_format` — the message points at the model's support:

```java
try {
    client.chat().completions().parse(request, Country.class);
} catch (StructuredOutputError e) {
    System.err.println(e.getMessage());
    // "… the model returned text that is not valid JSON … Check the model's
    //  support on the Models page (https://app.meshapi.ai/…/models) …"
}
```

Check a model's `supports_structured_output` flag via `client.models()`, or on the
Models page in your dashboard. `parse` is non-streaming.

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

## Audio (TTS, STT, voices)

```java
import com.meshapi.sdk.types.audio.*;

// Text-to-speech — returns raw audio bytes
byte[] audioBytes = client.audio().synthesize(
    SpeechRequest.builder()
        .input("Hello from MeshAPI.")
        .model("sarvam/bulbul:v2")
        .voice("meera")
        .build()
);
Files.write(Paths.get("output.wav"), audioBytes);

// Speech-to-text — submit transcription job
byte[] fileData = Files.readAllBytes(Paths.get("audio.wav"));
TranscriptionResponse result = client.audio().transcribe(
    fileData, "audio.wav",
    new TranscriptionRequest()  // model, languageCode, etc.
);
System.out.println(result.text);

// Retrieve a previously submitted transcription
Map<String, Object> stored = client.audio().getTranscription("transcription-id");

// POST /v1/audio/translations — OpenAI-compatible standalone translation endpoint
// Translates speech in any language to English text.
AudioTranslationRequest translationParams = new AudioTranslationRequest();
translationParams.model = "openai/whisper-1";
TranscriptionResponse translated = client.audio().translateAudio(
    fileData, "audio.mp3", translationParams
);
System.out.println(translated.text);

// POST /v1/audio/transcriptions/translate — legacy alias (still works)
TranscriptionResponse legacyTranslated = client.audio().translate(
    fileData, "audio.mp3", new TranscriptionTranslateRequest()
);

// List available voices
VoicesResponse voices = client.audio().listVoices(null);

// Get a specific voice
Voice voice = client.audio().getVoice("voice-id");
```

## Video generation

```java
import com.meshapi.sdk.types.video.*;

// Submit a video generation task
CreateVideoGenerationResponse task = client.videos().generate(
    VideoGenerationRequest.builder()
        .model("byteplus/dreamina-seedance-2-0")
        .addContent(VideoContentItem.text("A serene mountain lake at sunrise"))
        .build()
);
System.out.println("Task ID: " + task.id);

// Poll until complete
while (true) {
    VideoTaskResponse status = client.videos().retrieve(task.id);
    if ("succeeded".equals(status.status) || "failed".equals(status.status)) break;
    Thread.sleep(5_000);
}

// List past generation tasks
VideoTaskListResponse listing = client.videos().list(20, null, null, null, null);
System.out.println(listing.total + " total tasks");
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
    CreateTemplateRequest.builder()
        .name("my-tpl")
        .system("You are helpful.")
        .teamId("team_abc123")   // optional: assign to a team
        .build());
client.templates().delete(tmpl.id);
```

## Web search

```java
import com.meshapi.sdk.types.websearch.*;

WebSearchResponse resp = client.web().search(
    WebSearchRequest.builder()
        .query("latest Mars rover discoveries")
        .includeAnswer(true)
        .maxResults(10)
        .build()
);
System.out.println(resp.provider + ": " + resp.answer);
resp.results.forEach(r -> System.out.println(r.url + "  " + r.title));
```

Gated server-side by `WEB_SEARCH_ENABLED`. Inspect `resp.provider` ("native" or "tavily") to see which engine served the request.

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
