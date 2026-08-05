package com.meshapi.sdk.resources;

import com.meshapi.sdk.RequestOptions;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.embeddings.EmbeddingsRequest;
import com.meshapi.sdk.types.embeddings.EmbeddingsResponse;

public class EmbeddingsResource {
    private final HttpClient http;

    public EmbeddingsResource(HttpClient http) {
        this.http = http;
    }

    public EmbeddingsResponse create(EmbeddingsRequest params) {
        return create(params, null);
    }

    /** Embeddings request with per-request options (e.g. {@code X-Request-Id}). */
    public EmbeddingsResponse create(EmbeddingsRequest params, RequestOptions options) {
        return http.post("/v1/embeddings", params, EmbeddingsResponse.class, options);
    }
}
