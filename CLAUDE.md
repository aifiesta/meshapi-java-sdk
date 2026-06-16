# MeshAPI Java SDK

Official Java client for the MeshAPI AI model gateway.

- **Group / artifact**: `ai.meshapi:meshapi-java-sdk`
- **Java version**: 17+
- **Runtime dependencies**: `jackson-databind`
- **Build tool**: Maven

## Project layout

```
java/
├── src/main/java/com/meshapi/sdk/
│   ├── MeshAPI.java              # Main client (builder pattern)
│   ├── MeshAPIError.java         # Exception type
│   ├── internal/
│   │   ├── HttpClient.java       # JDK HttpClient wrapper, retry, auth
│   │   └── SseParser.java        # Server-Sent Events parser
│   ├── resources/
│   │   ├── ChatResource.java
│   │   ├── CompletionsResource.java
│   │   ├── ResponsesResource.java
│   │   ├── EmbeddingsResource.java
│   │   ├── CompareResource.java
│   │   ├── FilesResource.java    # Batch file objects
│   │   ├── RagResource.java      # RAG upload / embed / search
│   │   ├── BatchesResource.java
│   │   ├── ModelsResource.java
│   │   ├── TemplatesResource.java
│   │   └── ImagesResource.java
│   └── types/                    # One subpackage per resource area
│       ├── chat/
│       ├── rag/                  # InitUploadRequest/Response, SearchRequest/Response, …
│       └── …
├── src/test/                     # Unit tests
├── livetests/                    # Live tests (separate Maven module)
└── pom.xml
```

## Common tasks

### Build and run unit tests

```bash
mvn clean verify
```

### Run only unit tests (skip integration)

```bash
mvn test
```

### Install the SDK to the local Maven repository

Required before running live tests (they depend on the installed artifact):

```bash
mvn install -DskipTests
```

### Adding a new resource

1. Create a `types/<area>/` subpackage with request POJOs (`@JsonInclude(NON_NULL)` + builder) and response POJOs (`@JsonIgnoreProperties(ignoreUnknown = true)` + public fields).
2. Create `resources/<Name>Resource.java` using `http.get`, `http.post`, etc. from `internal/HttpClient`.
3. Add a private `final <Name>Resource <name>` field to `MeshAPI`.
4. Initialise it in the `MeshAPI(Builder)` constructor.
5. Add a public `<name>()` getter.
6. Follow the pattern in `resources/TemplatesResource.java`.

---

## Live tests

Live tests hit a real MeshAPI backend. They live in `livetests/`, a separate Maven module that depends on the SDK installed locally.

### Prerequisites

- Java 17+, Maven 3.8+.
- A running MeshAPI instance (default `http://localhost:8000`), **or** point at the dev API.
- A valid data-plane API key (`rsk_...`).
- The SDK installed to the local Maven repo (`mvn install -DskipTests` from `java/`).

### Environment variables

Create `java/.env.livetest` (read automatically by `LiveTestBase`) or export the variables in your shell before running tests.

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `MESHAPI_BASE_URL` | No | `http://localhost:8000` | Base URL of the MeshAPI gateway |
| `MESHAPI_TOKEN` | **Yes** | hardcoded dev key | Data-plane API key (`rsk_...`) |
| `MESHAPI_MODEL` | No | `openai/gpt-4o-mini` | Primary model used in chat/stream tests |
| `MESHAPI_SECOND_MODEL` | No | `anthropic/claude-haiku-4.5` | Second model for compare tests |
| `MESHAPI_EMBEDDINGS_MODEL` | No | `openai/text-embedding-3-small` | Model used in embeddings tests |
| `MESHAPI_IMAGE_GEN_MODEL` | No | _(skipped if unset)_ | Image generation model; test skipped if blank |
| `MESHAPI_IMAGE_URL` | No | _(skipped if unset)_ | Publicly accessible image URL for vision tests |
| `MESHAPI_REALTIME_MODEL` | No | `openai/gpt-realtime-mini` | Realtime-capable model used in WebSocket live tests |

Example `java/.env.livetest`:

```env
MESHAPI_BASE_URL=https://api-dev.meshapi.ai
MESHAPI_TOKEN=rsk_your_key_here
MESHAPI_MODEL=openai/gpt-4o-mini
MESHAPI_EMBEDDINGS_MODEL=openai/text-embedding-3-small
```

### Run all live tests

```bash
# 1. Install the SDK locally (once, or after any SDK changes)
mvn install -DskipTests

# 2. Run live tests
cd livetests
mvn test
```

### Run a single live test class

```bash
cd livetests
mvn test -Dtest=RagLiveTest
```

### Run a single test method

```bash
cd livetests
mvn test -Dtest="RagLiveTest#uploadEmbedSearch"
```

### Available live test classes

| Class | What it tests |
|-------|---------------|
| `ChatLiveTest` | Chat completions (basic, tools, multi-turn) |
| `StreamLiveTest` | Streaming chat and responses |
| `ModelsLiveTest` | Model listing |
| `TemplatesLiveTest` | Template CRUD lifecycle |
| `InferenceResourcesLiveTest` | Embeddings, responses |
| `ErrorsLiveTest` | 401/404 error handling |
| `FeatureMatrixLiveTest` | Cross-model feature matrix |
| `RagLiveTest` | RAG upload → embed → list → search |
| `RealtimeLiveTest` | WebSocket connect/close, session.created, session.update, error envelopes, audio frame typing |

### Available live test classes (updated)

| Class | What it tests |
|-------|---------------|
| `ChatLiveTest` | Chat completions (basic, tools, multi-turn) |
| `StreamLiveTest` | Streaming chat and responses |
| `ModelsLiveTest` | Model listing |
| `TemplatesLiveTest` | Template CRUD lifecycle |
| `InferenceResourcesLiveTest` | Embeddings, responses |
| `ErrorsLiveTest` | 401/404 error handling |
| `FeatureMatrixLiveTest` | Cross-model feature matrix |
| `RagLiveTest` | RAG upload → embed → list → search |
| `RealtimeLiveTest` | WebSocket connect/close, session lifecycle |
| `AudioLiveTest` | TTS synthesize, voice listing |
| `VideoLiveTest` | Video list, generate → retrieve |
| `CompareLiveTest` | Non-streaming compare, streaming compare |

---

## Contribution checklist

Every SDK change — however small — must include all of the following before merging:

1. **Live tests** — add or update `livetests/src/test/java/com/meshapi/livetest/<Name>LiveTest.java` to cover the new/changed behaviour.
2. **Unit tests** — if the change affects serialisation, retry logic, or error mapping, add a test in `src/test/`.
3. **README** — update `README.md` with a usage example for any new or changed public surface.
4. **meshapi-docs** — open a follow-up PR (or note in the PR description) to update the [meshapi-docs](https://github.com/aifiesta/meshapi-docs) repository so the developer documentation stays in sync.

---

---

## Release

The Java SDK is published to Maven Central under `ai.meshapi:meshapi-java-sdk`. Publishing requires Maven Central credentials and GPG signing configured in `~/.m2/settings.xml`.

### Release checklist

1. **Bump the version** in `pom.xml`:
   ```xml
   <version>0.1.1</version>
   ```

2. **Build and verify**:
   ```bash
   mvn clean verify
   ```

3. **Deploy to Maven Central staging**:
   ```bash
   mvn clean deploy -P release
   ```

4. **Commit the version bump, tag, and push**:
   ```bash
   git add pom.xml
   git commit -m "chore: release v0.1.1"
   git tag v0.1.1
   git push origin main
   git push origin v0.1.1
   ```

5. **Log in to OSSRH** at `https://s01.oss.sonatype.org`, find the staging repository, close it, then release it.

6. **Verify** the release is available on Maven Central (may take 10–30 min):
   ```bash
   mvn dependency:get -Dartifact=ai.meshapi:meshapi-java-sdk:0.1.1
   ```

### RAG live test notes

`RagLiveTest#uploadEmbedSearch` does the following:
1. Calls `client.rag().initUpload(...)` with `embed=false`.
2. PUTs the file bytes directly to the returned `signedUrl` via the JDK `HttpClient`.
3. Waits up to 30 s for `uploadStatus=ready`.
4. Calls `client.rag().embed(...)` to trigger embedding.
5. Polls up to 90 s for `embeddingStatus=ready`.
6. Calls `client.rag().list(50, null)` and asserts the file appears.
7. Calls `client.rag().search(...)` scoped to the file ID and asserts non-empty results.
