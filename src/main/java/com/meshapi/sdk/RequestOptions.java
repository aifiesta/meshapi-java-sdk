package com.meshapi.sdk;

import java.util.regex.Pattern;

/**
 * Per-request options, accepted as an optional trailing argument by the main
 * inference methods (chat, responses, embeddings, compare, images).
 *
 * <p>Currently supports a client-supplied request ID, sent as the
 * {@code X-Request-Id} header. The backend echoes it on the response and in
 * error envelopes, which makes correlating client and server logs trivial.
 *
 * <pre>{@code
 * ChatCompletionResponse resp = client.chat().completions().create(
 *         request, RequestOptions.withRequestId("checkout-42"));
 * }</pre>
 */
public final class RequestOptions {

    /**
     * The backend only honours request IDs matching this pattern
     * (1–64 chars of {@code A–Z a–z 0–9 . _ : -}); anything else is silently
     * ignored server-side, so the SDK rejects invalid IDs up front.
     */
    public static final Pattern REQUEST_ID_PATTERN = Pattern.compile("^[A-Za-z0-9._:-]{1,64}$");

    private final String requestId;

    private RequestOptions(Builder builder) {
        this.requestId = builder.requestId;
    }

    /** Shorthand for {@code RequestOptions.builder().requestId(id).build()}. */
    public static RequestOptions withRequestId(String requestId) {
        return builder().requestId(requestId).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /** The client-supplied request ID, or null if none was set. */
    public String getRequestId() {
        return requestId;
    }

    public static final class Builder {
        private String requestId;

        /**
         * Sets the request ID sent as the {@code X-Request-Id} header.
         *
         * @throws IllegalArgumentException if the ID does not match
         *         {@code ^[A-Za-z0-9._:-]{1,64}$} — the backend would silently
         *         ignore it, so the SDK fails fast instead.
         */
        public Builder requestId(String requestId) {
            if (requestId == null || !REQUEST_ID_PATTERN.matcher(requestId).matches()) {
                throw new IllegalArgumentException(
                        "Invalid request ID " + (requestId == null ? "null" : "\"" + requestId + "\"")
                        + ": must be 1-64 characters of A-Z, a-z, 0-9, '.', '_', ':' or '-' "
                        + "(the backend silently ignores anything else)");
            }
            this.requestId = requestId;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(this);
        }
    }
}
