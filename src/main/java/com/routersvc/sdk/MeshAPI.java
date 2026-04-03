package com.routersvc.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routersvc.sdk.internal.HttpClient;
import com.routersvc.sdk.resources.ChatResource;
import com.routersvc.sdk.resources.ModelsResource;
import com.routersvc.sdk.resources.TemplatesResource;

import java.time.Duration;

/**
 * RouterSVC SDK client.
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
    private final ModelsResource models;
    private final TemplatesResource templates;

    private MeshAPI(Builder builder) {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient http = new HttpClient(
                builder.javaHttpClient,
                mapper,
                builder.baseUrl,
                builder.token,
                builder.maxRetries,
                Duration.ofMillis(builder.timeoutMs)
        );
        this.chat = new ChatResource(http);
        this.models = new ModelsResource(http);
        this.templates = new TemplatesResource(http);
    }

    public ChatResource chat() { return chat; }
    public ModelsResource models() { return models; }
    public TemplatesResource templates() { return templates; }

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

        /** The RouterSVC gateway base URL (required). */
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }

        /** The Bearer token for this auth realm (required). */
        public Builder token(String token) { this.token = token; return this; }

        /** Request timeout in milliseconds (default 60_000). */
        public Builder timeoutMs(long ms) { this.timeoutMs = ms; return this; }

        /** Number of retry attempts on retryable errors (default 3, use 0 to disable). */
        public Builder maxRetries(int retries) { this.maxRetries = retries; return this; }

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
