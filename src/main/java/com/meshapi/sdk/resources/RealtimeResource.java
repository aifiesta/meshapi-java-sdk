package com.meshapi.sdk.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.realtime.RealtimeError;
import com.meshapi.sdk.types.realtime.RealtimeListener;
import com.meshapi.sdk.types.realtime.RealtimeMessage;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Provides access to the MeshAPI WebSocket realtime endpoint ({@code WSS /v1/realtime}).
 *
 * <p>Obtain via {@code client.realtime()}.
 *
 * <p>Auth is sent via the {@code Sec-WebSocket-Protocol} header as required by the
 * wire contract: {@code openai-realtime, Bearer <rsk_...>}.
 *
 * <pre>{@code
 * RealtimeListener listener = new RealtimeListener() {
 *     \@Override public void onOpen(RealtimeSession s)    { System.out.println("open"); }
 *     \@Override public void onMessage(RealtimeSession s, RealtimeMessage m) {
 *         System.out.println(m.getEvent().get("type"));
 *     }
 *     \@Override public void onError(RealtimeSession s, RealtimeError e) { e.printStackTrace(); }
 *     \@Override public void onClose(RealtimeSession s, int code, String reason) {}
 * };
 *
 * RealtimeSession session = client.realtime().connect("openai/gpt-4o-realtime-preview", listener).join();
 * session.send(Map.of("type", "session.update", "session", Map.of(...))).join();
 * // ... exchange frames ...
 * session.close();
 * }</pre>
 */
public class RealtimeResource {

    private static final String SDK_VERSION_HEADER = "X-MeshAPI-SDK";
    private static final String SDK_VERSION_VALUE  = "java/" + MeshAPI.VERSION;

    private final HttpClient   httpClient;
    private final ObjectMapper mapper;
    private final String       baseUrl;
    private final String       token;

    public RealtimeResource(HttpClient httpClient, ObjectMapper mapper, String baseUrl, String token) {
        this.httpClient = httpClient;
        this.mapper     = mapper;
        this.baseUrl    = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.token      = token;
    }

    /**
     * Opens an async WebSocket session for the given {@code model}.
     *
     * @param model    realtime-capable model ID, e.g. {@code "openai/gpt-4o-realtime-preview"}
     * @param listener callback handler for session events
     * @return a {@link CompletableFuture} that completes with the open {@link RealtimeSession}
     */
    public CompletableFuture<RealtimeSession> connect(String model, RealtimeListener listener) {
        String wsUrl = buildWsUrl(model);

        CompletableFuture<RealtimeSession> sessionFuture = new CompletableFuture<>();

        httpClient.newWebSocketBuilder()
                .header(SDK_VERSION_HEADER, SDK_VERSION_VALUE)
                .subprotocols("openai-realtime")
                .buildAsync(URI.create(wsUrl), new WebSocketListenerAdapter(listener, mapper, sessionFuture))
                .exceptionally(ex -> {
                    sessionFuture.completeExceptionally(ex);
                    return null;
                });

        return sessionFuture;
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private String buildWsUrl(String model) {
        String base = baseUrl
                .replace("https://", "wss://")
                .replace("http://",  "ws://");
        return base + "/v1/realtime?model=" + URLEncoder.encode(model, StandardCharsets.UTF_8)
                + "&api_key=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    /**
     * Adapts the JDK {@link WebSocket.Listener} interface to {@link RealtimeListener},
     * assembling fragmented frames and parsing JSON text frames.
     */
    private static class WebSocketListenerAdapter implements WebSocket.Listener {

        private final RealtimeListener listener;
        private final ObjectMapper mapper;
        private final CompletableFuture<RealtimeSession> sessionFuture;
        private final StringBuilder textAccumulator = new StringBuilder();

        private volatile RealtimeSession session;

        WebSocketListenerAdapter(RealtimeListener listener, ObjectMapper mapper,
                                 CompletableFuture<RealtimeSession> sessionFuture) {
            this.listener      = listener;
            this.mapper        = mapper;
            this.sessionFuture = sessionFuture;
        }

        @Override
        public void onOpen(WebSocket ws) {
            session = new RealtimeSession(ws, mapper);
            sessionFuture.complete(session);
            listener.onOpen(session);
            ws.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            textAccumulator.append(data);
            ws.request(1);
            if (!last) return null; // wait for more fragments

            String raw = textAccumulator.toString();
            textAccumulator.setLength(0);

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> evt = mapper.readValue(raw, Map.class);

                if ("error".equals(evt.get("type"))) {
                    // Parse the error envelope and deliver to onError.
                    @SuppressWarnings("unchecked")
                    Map<String, Object> errMap = (Map<String, Object>) evt.getOrDefault("error", Collections.emptyMap());
                    String code      = (String) errMap.getOrDefault("code",    "unknown");
                    String message   = (String) errMap.getOrDefault("message", "realtime error");
                    String param     = (String) errMap.get("param");
                    String requestId = (String) evt.get("request_id");
                    RealtimeError re = new RealtimeError(code, message, param, requestId);
                    listener.onError(session, re);
                } else {
                    listener.onMessage(session, RealtimeMessage.text(raw, Collections.unmodifiableMap(evt)));
                }
            } catch (Exception e) {
                // Non-JSON frame — deliver as a plain text message without event.
                listener.onMessage(session, RealtimeMessage.text(raw, null));
            }
            return null;
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket ws, ByteBuffer data, boolean last) {
            ws.request(1);
            if (!last) return null; // simplified: assumes single-frame binary messages

            byte[] bytes = new byte[data.remaining()];
            data.get(bytes);
            listener.onMessage(session, RealtimeMessage.audio(bytes));
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            if (session != null) {
                listener.onClose(session, statusCode, reason);
            }
            return null;
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            if (!sessionFuture.isDone()) {
                sessionFuture.completeExceptionally(error);
                return;
            }
            if (session != null) {
                RealtimeError re = new RealtimeError(
                        "provider_error",
                        error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName(),
                        null, null
                );
                listener.onError(session, re);
            }
        }
    }
}
