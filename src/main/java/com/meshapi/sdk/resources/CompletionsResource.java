package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;

import java.util.Iterator;

public class CompletionsResource {

    private final HttpClient http;

    public CompletionsResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Sends a non-streaming chat completion request.
     */
    public ChatCompletionResponse create(ChatCompletionRequest params) {
        params.setStream(false);
        return http.post("/v1/chat/completions", params, ChatCompletionResponse.class);
    }

    /**
     * Opens a streaming chat completion.
     *
     * <p>Returns an {@link Iterator} of SSE chunks. Iterate until {@link Iterator#hasNext()}
     * returns false. A mid-stream error causes {@link com.meshapi.sdk.MeshAPIError}
     * to be thrown from {@link Iterator#next()}.
     *
     * <p><strong>Streams do not retry.</strong> Catch the error and restart a new
     * {@link #stream} call if reconnection is needed.
     */
    public Iterator<ChatCompletionChunk> stream(ChatCompletionRequest params) {
        params.setStream(true);
        return http.stream("/v1/chat/completions", params);
    }
}
