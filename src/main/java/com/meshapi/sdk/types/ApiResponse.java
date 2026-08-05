package com.meshapi.sdk.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Base class for top-level API response types.
 *
 * <p>Carries the server-assigned request ID (the {@code X-Request-Id} response
 * header, format {@code req_<ULID>} unless the caller supplied their own via
 * {@link com.meshapi.sdk.RequestOptions}). Populated by the SDK's HTTP layer
 * after deserialization; it is never part of the JSON body and is excluded
 * from Jackson serialization.
 */
public abstract class ApiResponse {

    @JsonIgnore
    private String requestId;

    /** The {@code X-Request-Id} of the HTTP response this object came from, or null. */
    @JsonIgnore
    public String getRequestId() {
        return requestId;
    }

    /** Internal: set by the SDK's HTTP layer. Not part of the public API contract. */
    @JsonIgnore
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
