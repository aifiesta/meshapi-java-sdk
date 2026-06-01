package com.meshapi.sdk.types.realtime;

/**
 * Delivered by the server inside a {@code {"type":"error",...}} text frame
 * before the WebSocket is closed. Extends {@link RuntimeException} so it can
 * be thrown from listener callbacks without requiring checked-exception boilerplate.
 */
public class RealtimeError extends RuntimeException {

    private final String code;
    private final String param;
    private final String requestId;

    public RealtimeError(String code, String message, String param, String requestId) {
        super("realtime[" + code + "]: " + message);
        this.code = code;
        this.param = param;
        this.requestId = requestId;
    }

    /** Snake_case error code, e.g. {@code "invalid_api_key"}, {@code "insufficient_quota"}. */
    public String getCode() { return code; }

    /** Offending parameter, if any; otherwise {@code null}. */
    public String getParam() { return param; }

    /** Server-assigned session request ID for log correlation; may be {@code null}. */
    public String getRequestId() { return requestId; }
}
