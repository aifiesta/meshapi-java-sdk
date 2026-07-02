package com.meshapi.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.models.ModelInfo;
import com.meshapi.sdk.types.models.ModelSearchParams;
import com.meshapi.sdk.types.models.ModelsPage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ModelsResource {

    private static final TypeReference<List<ModelInfo>> MODEL_LIST = new TypeReference<>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http;

    public ModelsResource(HttpClient http) {
        this.http = http;
    }

    /** Returns all available models. Pass {@code Boolean.TRUE}/{@code Boolean.FALSE} to filter by free tier. */
    public List<ModelInfo> list(Boolean free) {
        return list(free, null, null);
    }

    /**
     * Returns models filtered by free tier, type, and/or provider.
     *
     * @param free     {@code true} for free models, {@code false} for paid; {@code null} for all
     * @param type     model type filter: {@code text}, {@code embedding}, {@code image}, {@code audio}, {@code video};
     *                 or {@code null} for all
     * @param provider provider name filter (e.g. {@code "openai"}); or {@code null} for all
     */
    public List<ModelInfo> list(Boolean free, String type, String provider) {
        StringBuilder qs = new StringBuilder();
        if (free != null) append(qs, "free", free.toString().toLowerCase());
        if (type != null) append(qs, "type", encode(type));
        if (provider != null) append(qs, "provider", encode(provider));
        Object[] arr = http.get("/v1/models", qs.length() > 0 ? qs.toString() : null, Object[].class);
        return MAPPER.convertValue(arr, MODEL_LIST);
    }

    /** Returns only free-tier models. */
    public List<ModelInfo> free() {
        Object[] arr = http.get("/v1/models/free", Object[].class);
        return MAPPER.convertValue(arr, MODEL_LIST);
    }

    /** Returns only paid-tier models. */
    public List<ModelInfo> paid() {
        Object[] arr = http.get("/v1/models/paid", Object[].class);
        return MAPPER.convertValue(arr, MODEL_LIST);
    }

    /**
     * Search models (GET /v1/models/search).
     *
     * <p>Supports 10 query parameters: q, free, discounted, input_modality, output_modality,
     * brand, sort, order, limit, offset.
     *
     * @param params search parameters (all optional)
     * @return a page of matching models
     */
    public ModelsPage search(ModelSearchParams params) {
        StringBuilder qs = new StringBuilder();
        if (params.q != null) append(qs, "q", encode(params.q));
        if (params.free != null) append(qs, "free", params.free.toString().toLowerCase());
        if (params.discounted != null) append(qs, "discounted", params.discounted.toString().toLowerCase());
        if (params.inputModality != null) {
            for (String v : params.inputModality) append(qs, "input_modality", encode(v));
        }
        if (params.outputModality != null) {
            for (String v : params.outputModality) append(qs, "output_modality", encode(v));
        }
        if (params.brand != null) {
            for (String v : params.brand) append(qs, "brand", encode(v));
        }
        if (params.sort != null) append(qs, "sort", encode(params.sort));
        if (params.order != null) append(qs, "order", encode(params.order));
        if (params.limit != null) append(qs, "limit", String.valueOf(params.limit));
        if (params.offset != null) append(qs, "offset", String.valueOf(params.offset));
        return http.get("/v1/models/search", qs.length() > 0 ? qs.toString() : null, ModelsPage.class);
    }

    /**
     * Get a single model by ID (GET /v1/models/{model_id}).
     *
     * <p>The backend route is {@code /v1/models/{model_id:path}}, so slashes in
     * provider-prefixed IDs like {@code "openai/gpt-4o"} are kept literal.
     * Other reserved characters are percent-encoded.
     *
     * @param modelId the model ID (e.g. {@code "openai/gpt-4o-mini"})
     * @return the model info
     */
    public ModelInfo get(String modelId) {
        // Keep "/" literal (path-style ID like "openai/gpt-4o"); encode everything else
        String encoded = URLEncoder.encode(modelId, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%2F", "/");
        return http.get("/v1/models/" + encoded, ModelInfo.class);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static void append(StringBuilder sb, String key, String value) {
        if (sb.length() > 0) sb.append("&");
        sb.append(key).append("=").append(value);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
