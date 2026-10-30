package com.meshapi.sdk.resources;

import com.meshapi.sdk.\u0069nternal.HttpClient;
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
     * @param params the Responses request
     * @return the completed response
     */
    public ResponsesResponse create(ResponsesRequest params) {
        params.stream = false;
        return http.post("/v1/responses", params, ResponsesResponse.class);
    }

    /**
     * Creates a response and streams it as server-sent events.
     *
     * @param params the Responses request
     * @return an iterator over stream events
     */
    public Iterator<ResponsesStreamEvent> stream(ResponsesRequest params) {
        params.stream = true;
        return http.streamJson("/v1/responses", params, ResponsesStreamEvent.class);
    }

    /**
     * Counts the input tokens of a Responses request before sending it.
     *
     * <p>Calls {@code POST /v1/responses/input_tokens}. The request body has the same
     * shape as {@link #create(ResponsesRequest)}. No inference runs, nothing is billed,
     * and no usage row is recorded.</p>
     *
     * <pre>{@code
     * ResponsesInputTokensResponse count = client.responses().inputTokens(request);
     * int tokens = count.inputTokens;
     * }</pre>
     *
     * @param params the Responses request to count
     * @return the input token count
     */
    public ResponsesInputTokensResponse inputTokens(ResponsesRequest params) {
        return http.post("/v1/responses/input_tokens", params, ResponsesInputTokensResponse.class);
    }
}
