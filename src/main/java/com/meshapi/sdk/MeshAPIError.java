package com.meshapi.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when MeshAPI returns a non-2xx response or sends an error frame mid-stream.
 */
public class MeshAPIError extends RuntimeException {

    /** HTTP status code (0 for stream/parse errors). */
    private final int status;
    /** Machine-readable error code slug (e.g. "unauthorized"). */
    private final String errorCode;
    /** Request tracing ID (req_&lt;ULID&gt;). */
    private final String requestId;
    /** Validation error details (non-empty on 422 responses). */
    private final List<Object> details;
    /** Retry delay in seconds (set on 429 responses). */
    private final Integer retryAfterSeconds;

    public MeshAPIError(String message, int status, String errorCode,
                              String requestId, List<Object> details,
                              Integer retryAfterSeconds) {
        super(message);
        this.status = status;
        this.errorCode = errorCode != null ? errorCode : "unknown_error";
        this.requestId = requestId != null ? requestId : "";
        this.details = details != null ? details : new ArrayList<>();
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getStatus() { return status; }
    public String getErrorCode() { return errorCode; }
    public String getRequestId() { return requestId; }
    public List<Object> getDetails() { return details; }
    public Integer getRetryAfterSeconds() { return retryAfterSeconds; }

    @Override
    public String toString() {
        return String.format("MeshAPIError(status=%d, code=%s, requestId=%s): %s",
                status, errorCode, requestId, getMessage());
    }

    // -----------------------------------------------------------------------
    // Factory
    // -----------------------------------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Parses a MeshAPIError from an HTTP response.
     * Handles non-JSON bodies gracefully.
     */
    public static MeshAPIError fromResponse(HttpResponse<String> response) {
        int status = response.statusCode();
        String requestId = response.headers().firstValue("x-request-id").orElse("");
        String contentType = response.headers().firstValue("content-type").orElse("");
        String body = response.body();

        if (!contentType.contains("application/json")) {
            String msg = body != null && body.length() > 500 ? body.substring(0, 500) : body;
            if (msg == null || msg.isBlank()) msg = "HTTP " + status;
            return new MeshAPIError(msg, status, "parse_error", requestId, null, null);
        }

        try {
            JsonNode root = MAPPER.readTree(body);
            JsonNode errorNode = root.path("error");
            String reqId = root.path("request_id").asText(requestId);
            String code = errorNode.path("code").asText("unknown_error");
            String message = errorNode.path("message").asText("HTTP " + status);

            List<Object> details = new ArrayList<>();
            JsonNode detailsNode = errorNode.path("details");
            if (detailsNode.isArray()) {
                for (JsonNode d : detailsNode) {
                    details.add(MAPPER.convertValue(d, Object.class));
                }
            }

            Integer retryAfter = null;
            JsonNode retryNode = errorNode.path("retry_after_seconds");
            if (!retryNode.isMissingNode() && !retryNode.isNull()) {
                retryAfter = retryNode.asInt();
            }

            return new MeshAPIError(message, status, code, reqId, details, retryAfter);
        } catch (Exception e) {
            String msg = body != null && body.length() > 500 ? body.substring(0, 500) : body;
            if (msg == null || msg.isBlank()) msg = "HTTP " + status;
            return new MeshAPIError(msg, status, "parse_error", requestId, null, null);
        }
    }

    /** Creates an error representing a mid-stream connection failure. */
    public static MeshAPIError streamInterrupted(String cause) {
        return new MeshAPIError("Stream interrupted: " + cause,
                0, "stream_interrupted", "", null, null);
    }

    /** Creates an error from an in-stream error frame. */
    public static MeshAPIError fromStreamFrame(String code, String message) {
        return new MeshAPIError(message, 0,
                code != null && !code.isBlank() ? code : "upstream_error",
                "", null, null);
    }
}
