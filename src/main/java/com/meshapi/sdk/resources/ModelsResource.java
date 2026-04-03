package com.meshapi.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.models.ModelInfo;

import java.util.List;

public class ModelsResource {

    private static final TypeReference<List<ModelInfo>> MODEL_LIST = new TypeReference<>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http;

    public ModelsResource(HttpClient http) {
        this.http = http;
    }

    /** Returns all available models. Pass {@code Boolean.TRUE}/{@code Boolean.FALSE} to filter. */
    public List<ModelInfo> list(Boolean free) {
        String qs = free != null ? "free=" + free.toString().toLowerCase() : null;
        Object[] arr = http.get("/v1/models", qs, Object[].class);
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
}
