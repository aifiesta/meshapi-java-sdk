package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.moderations.ModerationRequest;
import com.meshapi.sdk.types.moderations.ModerationResponse;

/**
 * Resource for POST /v1/moderations.
 *
 * <pre>{@code
 * ModerationResponse resp = client.moderations().create(
 *     ModerationRequest.builder()
 *         .input("This text might be harmful.")
 *         .build()
 * );
 * boolean flagged = resp.results.get(0).flagged;
 * }</pre>
 */
public class ModerationsResource {
    private final HttpClient http;

    public ModerationsResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Classifies text and/or images for harmful content.
     *
     * @param req the moderation request (input is required)
     * @return the moderation response with per-category flags and scores
     */
    public ModerationResponse create(ModerationRequest req) {
        return http.post("/v1/moderations", req, ModerationResponse.class);
    }
}
