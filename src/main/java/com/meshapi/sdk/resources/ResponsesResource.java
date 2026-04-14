package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.responses.ResponsesRequest;
import com.meshapi.sdk.types.responses.ResponsesResponse;

import java.util.Iterator;

/**
 * Handles POST /v1/responses — higher-level inference for reasoning models.
 * Requires a data-plane API key ({@code rsk_...}).
 *
 * <p>Streaming reuses {@link ChatCompletionChunk} — the SSE format is identical
 * to chat/completions.
 */
public class ResponsesResource {

    private final HttpClient http;

    public ResponsesResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Non-streaming request. Returns the full response.
     *
     * <pre>{@code
     * ResponsesResponse resp = client.responses().create(
     *     ResponsesRequest.builder()
     *         .model("openai/o4-mini")
     *         .input("Explain the halting problem simply.")
     *         .reasoning("medium")
     *         .build()
     * );
     * System.out.println(resp.choices.get(0).message.content);
     * }</pre>
     */
    public ResponsesResponse create(ResponsesRequest params) {
        return http.post("/v1/responses", params.withStream(false), ResponsesResponse.class);
    }

    /**
     * Streaming request. Returns an iterator of SSE chunks (same format as chat/completions).
     *
     * <p>Streams are NOT retried. Catch {@link com.meshapi.sdk.MeshAPIError}
     * and restart a new {@code stream()} call if reconnection is needed.
     *
     * <pre>{@code
     * Iterator<ChatCompletionChunk> it = client.responses().stream(
     *     ResponsesRequest.builder()
     *         .model("openai/o4-mini")
     *         .input(List.of(ChatMessage.user("Tell me a story.")))
     *         .build()
     * );
     * while (it.hasNext()) {
     *     String delta = it.next().choices.get(0).delta.content;
     *     if (delta != null) System.out.print(delta);
     * }
     * }</pre>
     */
    public Iterator<ChatCompletionChunk> stream(ResponsesRequest params) {
        return http.stream("/v1/responses", params.withStream(true));
    }
}
