package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.compare.CompareRequest;
import com.meshapi.sdk.types.compare.CompareResponse;
import com.meshapi.sdk.types.compare.CompareStreamEvent;

import java.util.Iterator;

public class CompareResource {
    private final HttpClient http;

    public CompareResource(HttpClient http) {
        this.http = http;
    }

    public CompareResponse create(CompareRequest params) {
        params.stream = false;
        return http.post("/v1/chat/compare", params, CompareResponse.class);
    }

    public Iterator<CompareStreamEvent> stream(CompareRequest params) {
        params.stream = true;
        return http.streamJson("/v1/chat/compare", params, CompareStreamEvent.class);
    }
}
