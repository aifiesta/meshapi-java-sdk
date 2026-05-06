package com.meshapi.sdk.resources;

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
        params.stream = false;
        return http.post("/v1/responses", params, ResponsesResponse.class);
    }

    public Iterator<ResponsesStreamEvent> stream(ResponsesRequest params) {
        params.stream = true;
        return http.streamJson("/v1/responses", params, ResponsesStreamEvent.class);
    }
}
