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
import com.meshapi.sdk.resilience.FallbackConfig;
import com.meshapi.sdk.resilience.FallbackEvent;
import com.meshapi.sdk.resilience.ResilienceEvent;
import com.meshapi.sdk.resilience.RetryPolicy;

import java.time.Duration;
import java.util.function.Consumer;

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
        ObjectMapper mapper = new ObjectMapper();
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
                RetryPolicy.resolve(builder.retry, builder.maxRetries),
                builder.fallback,
                builder.logger,
                builder.debug,
                Duration.ofMillis(builder.timeoutMs)
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
        private Integer maxRetries;
        private RetryPolicy retry;
        private FallbackConfig fallback;
        private Consumer<ResilienceEvent> logger;
        private boolean debug;
        private java.net.http.HttpClient javaHttpClient;

        /** The MeshAPI gateway base URL (required). */
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }

        /** The Bearer token for this auth realm (required). */
        public Builder token(String token) { this.token = token; return this; }

        /** Request timeout in milliseconds (default 60_000). */
        public Builder timeoutMs(long ms) { this.timeoutMs = ms; return this; }

        /**
         * Number of retry attempts on retryable errors (default 3, use 0 to disable).
         *
         * @deprecated Use {@link #retry(RetryPolicy)} — this alias maps onto
         *     {@code RetryPolicy.maxRetries}, and an explicit
         *     {@code retry(...)} value wins.
         */
        @Deprecated
        public Builder maxRetries(int retries) { this.maxRetries = retries; return this; }

        /**
         * Transport retry policy: which statuses to retry, backoff shape,
         * whether to honour {@code Retry-After}, and (opt-in) network-error
         * retry. Streaming requests are never retried.
         */
        public Builder retry(RetryPolicy retry) { this.retry = retry; return this; }

        /**
         * Client-side model-fallback chain for non-streaming
         * {@code chat().completions().create(...)}: when the primary model's
         * request exhausts its retries on a transient error, the SDK re-issues
         * it against each model in the chain until one succeeds. Each hop
         * fires a {@link FallbackEvent}.
         */
        public Builder fallback(FallbackConfig fallback) { this.fallback = fallback; return this; }

        /**
         * Structured sink for resilience events — every transport retry, every
         * fallback hop, and every gateway-side routing outcome (parsed from
         * the {@code X-Mesh-Routing-*} response headers). Use this to pipe
         * into your own logging framework; use {@link #debug(boolean)} for
         * ready-made readable lines instead.
         */
        public Builder logger(Consumer<ResilienceEvent> logger) { this.logger = logger; return this; }

        /**
         * Print readable resilience lines to stderr
         * ({@code [meshapi] retrying POST …}). Gateway-routing lines are
         * printed only when interesting (a retry or a provider fallback
         * actually happened). Independent of {@link #logger(Consumer)}.
         * Default false.
         */
        public Builder debug(boolean debug) { this.debug = debug; return this; }

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
