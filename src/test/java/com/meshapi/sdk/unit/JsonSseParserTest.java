package com.meshapi.sdk.unit;

import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.internal.JsonSseParser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for {@link JsonSseParser#parseFrame} error-frame handling.
 *
 * <p>Only the standard error envelope {@code {"error": {"code", "message"}}} is a
 * fatal stream error. Compare stream events carry a per-model {@code "error"} STRING
 * for partial failures, which must be deserialized as data — not raised. Mirrors the
 * Go and Node SDK semantics.
 */
class JsonSseParserTest {

    @SuppressWarnings("unchecked")
    @Test
    void perModelErrorStringIsData_notFatal() {
        // Compare stream: one model reports a partial failure via an "error" string.
        String frame = "data: {\"model\":\"m\",\"error\":\"model timed out\"}\n\n";
        Map<String, Object> parsed = JsonSseParser.parseFrame(frame, Map.class);
        assertNotNull(parsed, "per-model error string frame should deserialize, not throw");
        assertEquals("model timed out", parsed.get("error"));
    }

    @Test
    void errorEnvelopeObjectIsFatal() {
        String frame = "data: {\"error\":{\"code\":\"upstream_error\",\"message\":\"boom\"}}\n\n";
        MeshAPIError ex = assertThrows(MeshAPIError.class,
                () -> JsonSseParser.parseFrame(frame, Map.class));
        assertEquals("upstream_error", ex.getErrorCode());
        assertEquals("boom", ex.getMessage());
    }

    @Test
    void doneSentinelReturnsNull() {
        assertNull(JsonSseParser.parseFrame("data: [DONE]\n\n", Map.class));
    }
}
