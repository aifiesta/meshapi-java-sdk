package com.meshapi.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.resources.ChatResource;
import com.meshapi.sdk.resources.CompareResource;
import com.meshapi.sdk.resources.EmbeddingsResource;
import com.meshapi.sdk.resources.BatchesResource;
import com.meshapi.sdk.resources.ModelsResource;
import com.meshapi.sdk.resources.ResponsesResource;
import com.meshapi.sdk.resources.TemplatesResource;
import com.meshapi.sdk.resources.ImagesResource;
import com.meshapi.sdk.resources.RagResource;
import com.meshapi.sdk.resources.RealtimeResource;
import com.meshapi.sdk.resources.AudioResource;
import com.meshapi.sdk.resources.VideosResource;
import com.meshapi.sdk.resources.ModerationsResource;
import com.meshapi.sdk.resources.RouterResource;
import com.meshapi.sdk.resources.WebResource;

import java.time.Duration;

/**
 * MeshAPI SDK client.
 *
 * <p>One instance = one auth realm. Use separate instances for different tokens
 * (e.g., {@code rsk_} key for inference, JWT for template management).
 *
 * <pre>{@code
 * MeshAPI client = MeshAPI.builder()
 *     .baseUrl("http://localhost:8000")
 *     .token("rsk_...")
 *     .build();
 *
 * ChatCompletionResponse resp = client.chat().completions().create(
 *     ChatCompletionRequest.builder()
 *         .model("openai/gpt-4o-mini")
 *         .addMessage(ChatMessage.user("Hello!"))
 *         .build()
 * );
 * }</pre>
 */
public class MeshAPI {

    /** Current SDK version. */
    public static final String VERSION = "0.1.0";

    /**
     * The dated MeshAPI contract version this SDK release was built against, sent as
     * {@code X-Mesh-Version} on every request.
     *
     * <p>Distinct from {@link #VERSION}: that identifies this SDK build, this identifies
     * the API contract it parses. They move independently — the contract changes only
     * when the SDK is updated for a newer response shape, which is rarer than a
     * release. Bump it together with whatever type changes that entails, and note it in
     * CHANGELOG.md so a caller can see which contract a release targets.
     *
     * <p>Not sent on the realtime WebSocket handshake: the gateway's versioning applies
     * to HTTP requests only, so a pin there would be a header nobody reads. Realtime
     * negotiates its version separately.
     */
    public static final String API_VERSION = "2026-08";

    private final ChatResource chat;
    private final ResponsesResource responses;
    private final EmbeddingsResource embeddings;
    private final CompareResource compare;
    private final BatchesResource batches;
    private final ModelsResource models;
    private final TemplatesResource templates;
    private final ImagesResource images;
    private final RagResource rag;
    private final RealtimeResource realtime;
    private final AudioResource audio;
    private final VideosResource videos;
    private final ModerationsResource moderations;
    private final RouterResource router;
    private final WebResource web;

    private MeshAPI(Builder builder) {
        ObjectMapper mapper = HttpClient.newDefaultMapper();
        java.net.http.HttpClient javaHttp = builder.javaHttpClient != null
                ? builder.javaHttpClient
                : java.net.http.HttpClient.newBuilder()
                        .connectTimeout(Duration.ofMillis(builder.timeoutMs))
                        .version(java.net.http.HttpClient.Version.HTTP_1_1)
                        .build();
        HttpClient http = new HttpClient(
                javaHttp,
                mapper,
                builder.baseUrl,
                builder.token,
                builder.maxRetries,
                Duration.ofMillis(builder.timeoutMs),
                builder.apiVersion
        );
        this.chat = new ChatResource(http);
        this.responses = new ResponsesResource(http);
        this.embeddings = new EmbeddingsResource(http);
        this.compare = new CompareResource(http);
        this.batches = new BatchesResource(http);
        this.models = new ModelsResource(http);
        this.templates = new TemplatesResource(http);
        this.images = new ImagesResource(http);
        this.rag = new RagResource(http);
        this.realtime = new RealtimeResource(javaHttp, mapper, builder.baseUrl, builder.token);
        this.audio = new AudioResource(http);
        this.videos = new VideosResource(http);
        this.moderations = new ModerationsResource(http);
        this.router = new RouterResource(http);
        this.web = new WebResource(http);
    }

    public ChatResource chat() { return chat; }
    public ResponsesResource responses() { return responses; }
    public EmbeddingsResource embeddings() { return embeddings; }
    public CompareResource compare() { return compare; }
    public BatchesResource batches() { return batches; }
    public ModelsResource models() { return models; }
    public TemplatesResource templates() { return templates; }
    public ImagesResource images() { return images; }
    public RagResource rag() { return rag; }
    public RealtimeResource realtime() { return realtime; }
    public AudioResource audio() { return audio; }
    public VideosResource videos() { return videos; }
    public ModerationsResource moderations() { return moderations; }
    public RouterResource router() { return router; }
    public WebResource web() { return web; }

    public static Builder builder() { return new Builder(); }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    public static final class Builder {
        private String baseUrl;
        private String token;
        private long timeoutMs = 60_000;
        private int maxRetries = 3;
        private java.net.http.HttpClient javaHttpClient;
        // Defaulted to the constant rather than left null, which is what makes an
        // explicit apiVersion(null) meaningful: "never called" and "opted out" have to
        // be distinguishable, and this avoids a separate sentinel to tell them apart.
        private String apiVersion = API_VERSION;

        /** The MeshAPI gateway base URL (required). */
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }

        /** The Bearer token for this auth realm (required). */
        public Builder token(String token) { this.token = token; return this; }

        /** Request timeout in milliseconds (default 60_000). */
        public Builder timeoutMs(long ms) { this.timeoutMs = ms; return this; }

        /** Number of retry attempts on retryable errors (default 3, use 0 to disable). */
        public Builder maxRetries(int retries) { this.maxRetries = retries; return this; }

        /**
         * Pins the dated MeshAPI contract version sent as {@code X-Mesh-Version}.
         *
         * <p>Defaults to {@link MeshAPI#API_VERSION}, the version this SDK release was
         * built against. Pass a different label if you have migrated ahead of this
         * release, or {@code null} to send no header at all and be served the gateway's
         * baseline, whatever it may become.
         *
         * <p>The gateway rejects a version it does not serve with {@code 400
         * invalid_api_version} rather than falling back, so a typo cannot leave you
         * believing you are pinned when you are not.
         */
        public Builder apiVersion(String apiVersion) { this.apiVersion = apiVersion; return this; }

        /** Inject a custom {@link java.net.http.HttpClient} (useful for testing). */
        public Builder httpClient(java.net.http.HttpClient client) {
            this.javaHttpClient = client;
            return this;
        }

        public MeshAPI build() {
            if (baseUrl == null || baseUrl.isBlank()) throw new IllegalArgumentException("baseUrl is required");
            if (token == null || token.isBlank()) throw new IllegalArgumentException("token is required");
            return new MeshAPI(this);
        }
    }
}
