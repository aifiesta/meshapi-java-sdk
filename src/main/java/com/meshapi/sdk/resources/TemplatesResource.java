package com.meshapi.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.templates.CreateTemplateRequest;
import com.meshapi.sdk.types.templates.TemplateSummary;
import com.meshapi.sdk.types.templates.UpdateTemplateRequest;

import java.util.List;

public class TemplatesResource {

    private static final TypeReference<List<TemplateSummary>> TEMPLATE_LIST = new TypeReference<>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient http;

    public TemplatesResource(HttpClient http) {
        this.http = http;
    }

    public TemplateSummary create(CreateTemplateRequest params) {
        return http.post("/v1/templates", params, TemplateSummary.class);
    }

    public List<TemplateSummary> list() {
        Object[] arr = http.get("/v1/templates", Object[].class);
        return MAPPER.convertValue(arr, TEMPLATE_LIST);
    }

    public TemplateSummary get(String id) {
        return http.get("/v1/templates/" + id, TemplateSummary.class);
    }

    public TemplateSummary update(String id, UpdateTemplateRequest params) {
        return http.patch("/v1/templates/" + id, params, TemplateSummary.class);
    }

    public void delete(String id) {
        http.delete("/v1/templates/" + id);
    }
}
