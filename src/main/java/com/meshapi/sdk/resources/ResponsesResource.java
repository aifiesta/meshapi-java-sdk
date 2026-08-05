package com.meshapi.sdk.resources;

import com.meshapi.sdk.RequestOptions;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.responses.ResponsesRequest;
import com.meshapi.sdk.types.responses.ResponsesResponse;
import com.meshapi.sdk.types.responses.ResponsesStreamEvent;

import java.util.Iterator;

public class ResponsesResource {
    private final HttpClient http;

    public ResponsesResource(HttpClient http) {
        this.http = http;
    }

    public ResponsesResponse create(ResponsesRequest params) {
        return create(params, null);
    }

    /** Non-streaming response with per-request options (e.g. {@code X-Request-Id}). */
    public ResponsesResponse create(ResponsesRequest params, RequestOptions options) {
        params.stream = false;
        return http.post("/v1/responses", params, ResponsesResponse.class, options);
    }

    public Iterator<ResponsesStreamEvent> stream(ResponsesRequest params) {
        return stream(params, null);
    }

    /** Streaming response with per-request options (e.g. {@code X-Request-Id}). */
    public Iterator<ResponsesStreamEvent> stream(ResponsesRequest params, RequestOptions options) {
        params.stream = true;
        return http.streamJson("/v1/responses", params, ResponsesStreamEvent.class, options);
    }
}
