package com.meshapi.sdk.types.realtime;

import com.meshapi.sdk.resources.RealtimeSession;

/**
 * Callback interface for events on a {@link RealtimeSession}.
 *
 * <p>All methods have default no-op implementations so callers only override
 * the events they care about.
 */
public interface RealtimeListener {

    /** Called once the session is fully open and ready for bidirectional exchange. */
    default void onOpen(RealtimeSession session) {}

    /**
     * Called for every text or audio frame received from the server.
     * Error envelopes ({@code type=error}) are delivered to {@link #onError} instead.
     */
    default void onMessage(RealtimeSession session, RealtimeMessage message) {}

    /**
     * Called when the server sends a {@code {"type":"error",...}} envelope, or
     * when the underlying WebSocket connection fails unexpectedly.
     */
    default void onError(RealtimeSession session, RealtimeError error) {}

    /** Called when the connection is closed (either side initiated the close). */
    default void onClose(RealtimeSession session, int code, String reason) {}
}
