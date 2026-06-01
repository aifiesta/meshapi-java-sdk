package com.meshapi.sdk.types.realtime;

import java.util.Map;

/**
 * A single frame received from the MeshAPI realtime endpoint.
 *
 * <p>Exactly one of {@link #getText()} or {@link #getAudio()} is non-null per message.
 * Text frames also expose the parsed JSON as {@link #getEvent()}.
 */
public final class RealtimeMessage {

    private final String text;
    private final byte[] audio;
    private final Map<String, Object> event;

    private RealtimeMessage(String text, byte[] audio, Map<String, Object> event) {
        this.text = text;
        this.audio = audio;
        this.event = event;
    }

    /** Create a text frame message. */
    public static RealtimeMessage text(String raw, Map<String, Object> event) {
        return new RealtimeMessage(raw, null, event);
    }

    /** Create a binary (audio) frame message. */
    public static RealtimeMessage audio(byte[] data) {
        return new RealtimeMessage(null, data, null);
    }

    /** Raw JSON string for text frames; {@code null} for audio frames. */
    public String getText() { return text; }

    /** Raw audio bytes for binary frames; {@code null} for text frames. */
    public byte[] getAudio() { return audio; }

    /**
     * Parsed JSON object for text frames; {@code null} for audio frames.
     * The map is unmodifiable and uses the same key/value types as Jackson's
     * default deserialization (String keys, String/Number/Boolean/List/Map values).
     */
    public Map<String, Object> getEvent() { return event; }

    /** Returns {@code true} if this is a binary audio frame. */
    public boolean isAudio() { return audio != null; }

    @Override
    public String toString() {
        if (audio != null) return "RealtimeMessage{audio=" + audio.length + "B}";
        Object type = event != null ? event.get("type") : null;
        return "RealtimeMessage{type=" + type + "}";
    }
}
