package com.meshapi.sdk.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.types.realtime.RealtimeMessage;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * An active WebSocket session with the MeshAPI realtime endpoint.
 *
 * <p>Obtain an instance via {@link RealtimeResource#connect(String, com.meshapi.sdk.types.realtime.RealtimeListener)}.
 * All send methods are non-blocking and return a {@link CompletableFuture} that
 * completes when the underlying WebSocket frame has been enqueued for transmission.
 *
 * <p>Implements {@link AutoCloseable} for use in try-with-resources:
 * <pre>{@code
 * try (RealtimeSession session = client.realtime().connect("openai/gpt-4o-realtime-preview", listener).join()) {
 *     session.send(Map.of("type", "session.update", "session", Map.of(...))).join();
 * }
 * }</pre>
 */
public class RealtimeSession implements AutoCloseable {

    private final WebSocket webSocket;
    private final ObjectMapper mapper;

    RealtimeSession(WebSocket webSocket, ObjectMapper mapper) {
        this.webSocket = webSocket;
        this.mapper = mapper;
    }

    /**
     * Serialises {@code event} as JSON and sends it as a text WebSocket frame.
     *
     * @param event any Jackson-serialisable object (typically a {@code Map<String,Object>})
     * @return a future that completes when the frame has been queued for sending
     */
    public CompletableFuture<Void> send(Map<String, Object> event) {
        String json;
        try {
            json = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
        return webSocket.sendText(json, true).thenApply(ws -> null);
    }

    /**
     * Sends raw audio bytes as a binary WebSocket frame.
     *
     * @param audio raw PCM / encoded audio bytes
     * @return a future that completes when the frame has been queued for sending
     */
    public CompletableFuture<Void> sendAudio(byte[] audio) {
        return webSocket.sendBinary(ByteBuffer.wrap(audio), true).thenApply(ws -> null);
    }

    /**
     * Sends a WebSocket close frame with normal-closure code (1000) and
     * releases the underlying connection.
     *
     * @return a future that completes when the close handshake has been initiated
     */
    public CompletableFuture<Void> closeAsync() {
        return webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "").thenApply(ws -> null);
    }

    /**
     * Synchronous close for try-with-resources.  Blocks until the close frame
     * has been sent; any exception is silently swallowed.
     */
    @Override
    public void close() {
        try {
            closeAsync().join();
        } catch (Exception ignored) {
            // best-effort close — don't mask the real exception
        }
    }
}
