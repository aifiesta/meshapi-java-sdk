package com.meshapi.sdk.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

/**
 * Internal HTTP client wrapping {@link java.net.http.HttpClient} with
 * retry/backoff, auth headers, and JSON helpers.
 */
public class HttpClient {

    private static final Set<Integer> RETRY_STATUS_CODES = Set.of(429, 502, 503, 504);
    private static final int BACKOFF_BASE_MS = 500;
    private static final int BACKOFF_MAX_MS = 30_000;
    private static final String SDK_VERSION_HEADER = "X-MeshAPI-SDK";
    private static final String SDK_VERSION_VALUE = "java/" + MeshAPI.VERSION;

    private final java.net.http.HttpClient javaClient;
    private final ObjectMapper mapper;
    private final String baseUrl;
    private final String token;
    private final int maxRetries;

    public HttpClient(java.net.http.HttpClient javaClient, ObjectMapper mapper,
                      String baseUrl, String token, int maxRetries, Duration timeout) {
        this.javaClient = javaClient != null ? javaClient :
                java.net.http.HttpClient.newBuilder()
                        .connectTimeout(timeout)
                        .version(java.net.http.HttpClient.Version.HTTP_1_1)
                        .build();
        this.mapper = mapper;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.token = token;
        this.maxRetries = maxRetries;
    }

    // -----------------------------------------------------------------------
    // Public HTTP methods
    // -----------------------------------------------------------------------

    public <T> T get(String path, Class<T> responseType) {
        return get(path, null, responseType);
    }

    public <T> T get(String path, String queryString, Class<T> responseType) {
        String url = baseUrl + path + (queryString != null && !queryString.isBlank() ? "?" + queryString : "");
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .build();
        return execute(req, responseType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        return jsonRequest("POST", path, body, responseType);
    }

    public <T> T patch(String path, Object body, Class<T> responseType) {
        return jsonRequest("PATCH", path, body, responseType);
    }

    public void delete(String path) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Bearer " + token)
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .build();
        executeRaw(req); // returns response, we don't need the body
    }

    public Iterator<ChatCompletionChunk> stream(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                    .build();

            HttpResponse<java.io.InputStream> response =
                    javaClient.send(req, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body().readAllBytes());
                HttpResponse<String> errResp = toStringResponse(response, errorBody);
                throw MeshAPIError.fromResponse(errResp);
            }

            return new SseParser(response.body());
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("stream request failed: " + e.getMessage(), e);
        }
    }

    public <T> Iterator<T> streamJson(String path, Object body, Class<T> valueType) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                    .build();

            // Streaming: no retry
            HttpResponse<java.io.InputStream> response =
                    javaClient.send(req, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body().readAllBytes());
                HttpResponse<String> errResp = toStringResponse(response, errorBody);
                throw MeshAPIError.fromResponse(errResp);
            }

            return new JsonSseParser<>(response.body(), valueType);
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("stream request failed: " + e.getMessage(), e);
        }
    }

    public byte[] getBytes(String path) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .header("Authorization", "Bearer " + token)
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .build();
        try {
            HttpResponse<byte[]> response = javaClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 400) {
                HttpResponse<String> errResp = toStringResponse(response, new String(response.body()));
                throw MeshAPIError.fromResponse(errResp);
            }
            return response.body();
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("GET bytes failed: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    private <T> T jsonRequest(String method, String path, Object body, Class<T> responseType) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE);

            if ("PATCH".equals(method)) {
                builder.method("PATCH", HttpRequest.BodyPublishers.ofString(json));
            } else {
                builder.POST(HttpRequest.BodyPublishers.ofString(json));
            }
            return execute(builder.build(), responseType);
        } catch (MeshAPIError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(method + " " + path + " failed: " + e.getMessage(), e);
        }
    }

    private <T> T execute(HttpRequest req, Class<T> responseType) {
        HttpResponse<String> response = executeWithRetry(req);
        if (response.statusCode() >= 400) {
            throw MeshAPIError.fromResponse(response);
        }
        if (response.statusCode() == 204) {
            return null;
        }
        String ct = response.headers().firstValue("content-type").orElse("");
        if (!ct.contains("application/json")) {
            String body = response.body();
            throw new MeshAPIError(
                    body != null && body.length() > 500 ? body.substring(0, 500) : body,
                    response.statusCode(), "parse_error", "", null, null);
        }
        try {
            return mapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new MeshAPIError("JSON parse error: " + e.getMessage(),
                    response.statusCode(), "parse_error", "", null, null);
        }
    }

    private HttpResponse<String> executeRaw(HttpRequest req) {
        return executeWithRetry(req);
    }

    private HttpResponse<String> executeWithRetry(HttpRequest req) {
        int attempt = 0;
        while (true) {
            try {
                HttpResponse<String> response =
                        javaClient.send(req, HttpResponse.BodyHandlers.ofString());

                if (RETRY_STATUS_CODES.contains(response.statusCode()) && attempt < maxRetries) {
                    long delayMs = computeDelay(attempt,
                            retryAfterFromResponse(response));
                    Thread.sleep(delayMs);
                    attempt++;
                    continue;
                }
                return response;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("request interrupted", e);
            } catch (Exception e) {
                if (attempt < maxRetries) {
                    try { Thread.sleep(computeDelay(attempt, null)); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("interrupted during retry", ie);
                    }
                    attempt++;
                    continue;
                }
                throw new RuntimeException("request failed after retries: " + e.getMessage(), e);
            }
        }
    }

    private static long computeDelay(int attempt, Integer retryAfterSec) {
        long baseMs = retryAfterSec != null
                ? (long) retryAfterSec * 1000
                : (long) (BACKOFF_BASE_MS * Math.pow(2, attempt));
        long capped = Math.min(baseMs, BACKOFF_MAX_MS);
        double jitter = capped * (0.8 + Math.random() * 0.4); // ±20%
        return (long) jitter;
    }

    private static Integer retryAfterFromResponse(HttpResponse<String> resp) {
        return resp.headers().firstValue("retry-after")
                .map(v -> {
                    try { return (int) Math.ceil(Double.parseDouble(v)); }
                    catch (NumberFormatException e) { return null; }
                })
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> HttpResponse<String> toStringResponse(HttpResponse<T> original, String body) {
        // Adapter to reuse MeshAPIError.fromResponse(HttpResponse<String>)
        return new HttpResponse<>() {
            public int statusCode() { return original.statusCode(); }
            public HttpRequest request() { return original.request(); }
            public java.util.Optional<HttpResponse<String>> previousResponse() { return java.util.Optional.empty(); }
            public java.net.http.HttpHeaders headers() { return original.headers(); }
            public String body() { return body; }
            public java.util.Optional<javax.net.ssl.SSLSession> sslSession() { return java.util.Optional.empty(); }
            public URI uri() { return original.uri(); }
            public java.net.http.HttpClient.Version version() { return original.version(); }
        };
    }
}
