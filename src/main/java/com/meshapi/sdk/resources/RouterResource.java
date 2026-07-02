package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.router.RouterSelectRequest;
import com.meshapi.sdk.types.router.RouterSelectResponse;

/**
 * Resource for POST /v1/router/select.
 *
 * <pre>{@code
 * RouterSelectResponse resp = client.router().select(
 *     RouterSelectRequest.builder()
 *         .addMessage(ChatMessage.user("What is the capital of France?"))
 *         .build()
 * );
 * System.out.println("Selected model: " + resp.model);
 * }</pre>
 */
public class RouterResource {
    private final HttpClient http;

    public RouterResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Select the best model for a given conversation (POST /v1/router/select).
     *
     * @param req the router selection request
     * @return the router selection response with the chosen model
     */
    public RouterSelectResponse select(RouterSelectRequest req) {
        return http.post("/v1/router/select", req, RouterSelectResponse.class);
    }
}
