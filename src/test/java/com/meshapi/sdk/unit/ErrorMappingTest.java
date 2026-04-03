package com.meshapi.sdk.unit;

import com.meshapi.sdk.MeshAPIError;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMappingTest {

    private static HttpResponse<String> makeResponse(int status, String body, String contentType) {
        Map<String, List<String>> headerMap = Map.of(
                "content-type", List.of(contentType),
                "x-request-id", List.of("req_test")
        );
        HttpHeaders headers = HttpHeaders.of(headerMap, (k, v) -> true);

        return new HttpResponse<>() {
            public int statusCode() { return status; }
            public HttpRequest request() { return null; }
            public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
            public HttpHeaders headers() { return headers; }
            public String body() { return body; }
            public Optional<SSLSession> sslSession() { return Optional.empty(); }
            public URI uri() { return URI.create("http://localhost"); }
            public HttpClient.Version version() { return HttpClient.Version.HTTP_1_1; }
        };
    }

    @Test
    void fromResponse_401() {
        String body = "{\"error\":{\"code\":\"unauthorized\",\"message\":\"Invalid or missing API key.\"},\"request_id\":\"req_001\"}";
        MeshAPIError err = MeshAPIError.fromResponse(makeResponse(401, body, "application/json"));
        assertEquals(401, err.getStatus());
        assertEquals("unauthorized", err.getErrorCode());
        assertEquals("req_001", err.getRequestId());
        assertTrue(err.getMessage().contains("Invalid or missing API key"));
    }

    @Test
    void fromResponse_429WithRetryAfter() {
        String body = "{\"error\":{\"code\":\"rate_limit_exceeded\",\"message\":\"Rate limit exceeded.\",\"retry_after_seconds\":5},\"request_id\":\"req_429\"}";
        MeshAPIError err = MeshAPIError.fromResponse(makeResponse(429, body, "application/json"));
        assertEquals(429, err.getStatus());
        assertEquals("rate_limit_exceeded", err.getErrorCode());
        assertEquals(5, err.getRetryAfterSeconds());
    }

    @Test
    void fromResponse_422WithDetails() {
        String body = "{\"error\":{\"code\":\"validation_error\",\"message\":\"Validation failed.\",\"details\":[{\"type\":\"missing\",\"loc\":[\"body\",\"messages\"]}]},\"request_id\":\"req_422\"}";
        MeshAPIError err = MeshAPIError.fromResponse(makeResponse(422, body, "application/json"));
        assertEquals("validation_error", err.getErrorCode());
        assertEquals(1, err.getDetails().size());
    }

    @Test
    void fromResponse_htmlBody_parseError() {
        MeshAPIError err = MeshAPIError.fromResponse(makeResponse(502, "<html>Bad Gateway</html>", "text/html"));
        assertEquals("parse_error", err.getErrorCode());
        assertTrue(err.getMessage().contains("Bad Gateway"));
    }

    @Test
    void fromResponse_malformedJson_parseError() {
        MeshAPIError err = MeshAPIError.fromResponse(makeResponse(500, "{not json}", "application/json"));
        assertEquals("parse_error", err.getErrorCode());
    }

    @Test
    void streamInterrupted_factory() {
        MeshAPIError err = MeshAPIError.streamInterrupted("connection reset");
        assertEquals("stream_interrupted", err.getErrorCode());
        assertEquals(0, err.getStatus());
        assertTrue(err.getMessage().contains("connection reset"));
    }

    @Test
    void fromStreamFrame_factory() {
        MeshAPIError err = MeshAPIError.fromStreamFrame("upstream_error", "provider error");
        assertEquals("upstream_error", err.getErrorCode());
    }

    @Test
    void isRuntimeException() {
        MeshAPIError err = MeshAPIError.fromStreamFrame("x", "y");
        assertInstanceOf(RuntimeException.class, err);
    }

    @Test
    void toString_containsStatusAndCode() {
        MeshAPIError err = new MeshAPIError("msg", 404, "not_found", "req_x", null, null);
        String s = err.toString();
        assertTrue(s.contains("404"));
        assertTrue(s.contains("not_found"));
    }
}
