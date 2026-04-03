package com.routersvc.sdk.resources;

import com.routersvc.sdk.internal.HttpClient;
import com.routersvc.sdk.types.chat.ChatCompletionChunk;
import com.routersvc.sdk.types.chat.ChatCompletionRequest;
import com.routersvc.sdk.types.chat.ChatCompletionResponse;

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
        // Force stream=false
        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .messages(params.getMessages())
                .model(params.getModel())
                .stream(false)
                .build();
        return http.post("/v1/chat/completions", req, ChatCompletionResponse.class);
    }

    /**
     * Opens a streaming chat completion.
     *
     * <p>Returns an {@link Iterator} of SSE chunks. Iterate until {@link Iterator#hasNext()}
     * returns false. A mid-stream error causes {@link com.routersvc.sdk.RouterSvcApiError}
     * to be thrown from {@link Iterator#next()}.
     *
     * <p><strong>Streams do not retry.</strong> Catch the error and restart a new
     * {@link #stream} call if reconnection is needed.
     */
    public Iterator<ChatCompletionChunk> stream(ChatCompletionRequest params) {
        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .messages(params.getMessages())
                .model(params.getModel())
                .stream(true)
                .build();
        return http.stream("/v1/chat/completions", req);
    }
}
