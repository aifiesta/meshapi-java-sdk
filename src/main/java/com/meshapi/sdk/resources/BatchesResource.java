package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.batch.BatchListResponse;
import com.meshapi.sdk.types.batch.BatchObject;
import com.meshapi.sdk.types.batch.CreateBatchRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BatchesResource {
    private final HttpClient http;

    public BatchesResource(HttpClient http) {
        this.http = http;
    }

    public BatchObject create(CreateBatchRequest params) {
        return http.post("/v1/batches", params, BatchObject.class);
    }

    public BatchListResponse list(String after, Integer limit) {
        StringBuilder qs = new StringBuilder();
        if (after != null && !after.isBlank()) {
            qs.append("after=").append(URLEncoder.encode(after, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        if (limit != null) {
            if (qs.length() > 0) qs.append("&");
            qs.append("limit=").append(limit);
        }
        return http.get("/v1/batches", qs.length() == 0 ? null : qs.toString(), BatchListResponse.class);
    }

    public BatchObject get(String batchId) {
        String encoded = URLEncoder.encode(batchId, StandardCharsets.UTF_8).replace("+", "%20");
        return http.get("/v1/batches/" + encoded, BatchObject.class);
    }

    public BatchObject cancel(String batchId) {
        String encoded = URLEncoder.encode(batchId, StandardCharsets.UTF_8).replace("+", "%20");
        return http.post("/v1/batches/" + encoded + "/cancel", java.util.Map.of(), BatchObject.class);
    }
}
