package com.meshapi.sdk.resources;

import com.meshapi.sdk. internal.HttpClient;
import com.meshapi.sdk.types.responses.ResponsesInputTokensResponse;
import com.meshapi.sdk.types.responses.ResponsesRequest;
import com.meshapi.sdk.types.responses.ResponsesResponse;
import com.meshapi.sdk.types.responses.ResponsesStreamEvent;

import java.util.Iterator;

/**
 * Access to the Responses API ({@code /v1/responses}).
 */
public class ResponsesResource {
    private final HttpClient http;

    public ResponsesResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Creates a response and waits for the full result.
     *
     * @param params the request body
     * @return the completed response
     */
    public ResponsesResponse create(ResponsesRequest params) {
        params.stream = false;
        return http.post("/v1/responses", params, ResponsesResponse.class);
    }

    /**
     * Creates a response and streams its events as they arrive.
     *
     * @param params the request body
     * @return an iterator over the streamed events
     */
    public Iterator<ResponsesStreamEvent> stream(ResponsesRequest params) {
        params.stream = true;
        return http.streamJson("/v1/responses", params, ResponsesStreamEvent.class);
    }

    /**
     * Counts the input tokens a Responses request would consume, without running inference.
     *
     * <p>Calls {@code POST /v1/responses/input_tokens}. The request body has the same shape as
     * {@link #create(ResponsesRequest)}. Counting is not billed and does not create a usage record.
     * Depending on the routed provider, the count is either exact or a local estimate.</p>
     *
     * @param params the request body to count
     * @return the input token count
     */
    public ResponsesInputTokensResponse inputTokens(ResponsesRequest params) {
        return http.post("/v1/responses/input_tokens", params, ResponsesInputTokensResponse.class);
    }
}
