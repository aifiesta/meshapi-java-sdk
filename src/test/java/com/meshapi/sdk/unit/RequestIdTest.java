package com.meshapi.sdk.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.MeshAPIError;
import com.meshapi.sdk.RequestOptions;
import com.meshapi.sdk.internal.SseParser;
import com.meshapi.sdk.types.audio.TranscriptionRequest;
import com.meshapi.sdk.types.audio.TranscriptionResponse;
import com.meshapi.sdk.types.chat.ChatCompletionChunk;
import com.meshapi.sdk.types.chat.ChatCompletionRequest;
import com.meshapi.sdk.types.chat.ChatCompletionResponse;
import com.meshapi.sdk.types.chat.ChatMessage;
import com.meshapi.sdk.types.embeddings.EmbeddingsRequest;
import com.meshapi.sdk.types.embeddings.EmbeddingsResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Request-ID support: sending a client-supplied X-Request-Id via
 * {@link RequestOptions}, fail-fast validation, and reading the server's
 * X-Request-Id back on responses, streams, and errors.
 *
 * <p>Uses a local {@link HttpServer} — no live backend required.
 */
class RequestIdTest {

    private static final String CHAT_BODY =
            "{\"id\":\"chatcmpl-1\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"m\","
            + "\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"hi\"},"
            + "\"finish_reason\":\"stop\"}],"
            + "\"usage\":{\"prompt_tokens\":1,\"completion_tokens\":1,\"total_tokens\":2}}";

    private HttpServer server;
    private MeshAPI client;
    /** X-Request-Id header of the last request the server received (null when absent). */
    private final AtomicReference<String> receivedHeader = new AtomicReference<>();

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        client = MeshAPI.builder()
                .baseUrl("http://127.0.0.1:" + server.getAddress().getPort())
                .token("rsk_test")
                .maxRetries(0)
                .build();
        receivedHeader.set(null);
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private void respondWith(int status, String contentType, String responseRequestId, String body) {
        server.createContext("/", exchange -> {
            receivedHeader.set(exchange.getRequestHeaders().getFirst("X-Request-Id"));
            exchange.getResponseHeaders().set("Content-Type", contentType);
            if (responseRequestId != null) {
                exchange.getResponseHeaders().set("X-Request-Id", responseRequestId);
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
    }

    private static ChatCompletionRequest chatRequest() {
        return ChatCompletionRequest.builder()
                .model("openai/gpt-4o-mini")
                .addMessage(ChatMessage.user("hello"))
                .build();
    }

    // -----------------------------------------------------------------------
    // (1) requestId option sends the header
    // -----------------------------------------------------------------------

    @Test
    void requestOptions_sendsHeader_jsonPath() {
        respondWith(200, "application/json", "custom-id-1", CHAT_BODY);
        client.chat().completions().create(chatRequest(), RequestOptions.withRequestId("custom-id-1"));
        assertEquals("custom-id-1", receivedHeader.get());
    }

    @Test
    void noRequestOptions_sendsNoHeader() {
        respondWith(200, "application/json", "req_abc", CHAT_BODY);
        client.chat().completions().create(chatRequest());
        assertNull(receivedHeader.get());
    }

    @Test
    void requestOptions_sendsHeader_streamingPath() {
        String sse = "data: {\"id\":\"c1\",\"object\":\"chat.completion.chunk\",\"choices\":[]}\n\n"
                + "data: [DONE]\n\n";
        respondWith(200, "text/event-stream", "stream-id-1", sse);
        Iterator<ChatCompletionChunk> it = client.chat().completions()
                .stream(chatRequest(), RequestOptions.withRequestId("stream-id-1"));
        while (it.hasNext()) it.next();
        assertEquals("stream-id-1", receivedHeader.get());
    }

    @Test
    void requestOptions_sendsHeader_multipartPath() {
        respondWith(200, "application/json", "multi-id-1", "{\"text\":\"hi\"}");
        TranscriptionResponse resp = client.audio().transcribe(
                new byte[]{1, 2, 3}, "clip.mp3",
                TranscriptionRequest.builder().model("scribe").build(),
                RequestOptions.withRequestId("multi-id-1"));
        assertEquals("multi-id-1", receivedHeader.get());
        assertEquals("multi-id-1", resp.getRequestId());
    }

    // -----------------------------------------------------------------------
    // (2) invalid id throws IllegalArgumentException before any network call
    // -----------------------------------------------------------------------

    @Test
    void invalidRequestId_throwsBeforeNetworkCall() {
        // No server context is registered: any HTTP call would fail loudly.
        assertThrows(IllegalArgumentException.class, () -> RequestOptions.withRequestId("has spaces"));
        assertThrows(IllegalArgumentException.class, () -> RequestOptions.withRequestId(""));
        assertThrows(IllegalArgumentException.class, () -> RequestOptions.withRequestId(null));
        assertThrows(IllegalArgumentException.class, () -> RequestOptions.withRequestId("emoji-⚡"));
        assertThrows(IllegalArgumentException.class, () -> RequestOptions.withRequestId("x".repeat(65)));
        assertNull(receivedHeader.get(), "no request must reach the server");
    }

    @Test
    void validRequestId_charsetBoundaries() {
        assertEquals("A-Za-z0-9._:-", RequestOptions.withRequestId("A-Za-z0-9._:-").getRequestId());
        assertEquals("x".repeat(64), RequestOptions.withRequestId("x".repeat(64)).getRequestId());
    }

    // -----------------------------------------------------------------------
    // (3) successful response populates getRequestId() from the header
    // -----------------------------------------------------------------------

    @Test
    void successResponse_populatesRequestId() {
        respondWith(200, "application/json", "req_01HZXYZ", CHAT_BODY);
        ChatCompletionResponse resp = client.chat().completions().create(chatRequest());
        assertEquals("req_01HZXYZ", resp.getRequestId());
        assertEquals("chatcmpl-1", resp.id);
    }

    @Test
    void successResponse_noHeader_requestIdNull() {
        respondWith(200, "application/json", null, CHAT_BODY);
        ChatCompletionResponse resp = client.chat().completions().create(chatRequest());
        assertNull(resp.getRequestId());
    }

    @Test
    void embeddingsResponse_populatesRequestId() {
        respondWith(200, "application/json", "req_emb1",
                "{\"object\":\"list\",\"data\":[],\"model\":\"m\"}");
        EmbeddingsResponse resp = client.embeddings().create(
                EmbeddingsRequest.builder().model("m").input("x").build());
        assertEquals("req_emb1", resp.getRequestId());
    }

    @Test
    void streamHandle_exposesRequestId() {
        String sse = "data: {\"id\":\"c1\",\"object\":\"chat.completion.chunk\",\"choices\":[]}\n\n"
                + "data: [DONE]\n\n";
        respondWith(200, "text/event-stream", "req_stream1", sse);
        Iterator<ChatCompletionChunk> it = client.chat().completions().stream(chatRequest());
        assertInstanceOf(SseParser.class, it);
        assertEquals("req_stream1", ((SseParser) it).getRequestId());
    }

    @Test
    void requestId_notSerializedByJackson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse resp = mapper.readValue(CHAT_BODY, ChatCompletionResponse.class);
        resp.setRequestId("req_should_not_appear");
        String serialized = mapper.writeValueAsString(resp);
        assertFalse(serialized.contains("req_should_not_appear"), "requestId leaked into JSON: " + serialized);
        assertFalse(serialized.contains("requestId"), "requestId property leaked into JSON: " + serialized);
        // And a body containing request_id/requestId keys must not break deserialization.
        ChatCompletionResponse reparsed = mapper.readValue(
                "{\"id\":\"c2\",\"request_id\":\"req_x\",\"requestId\":\"req_y\"}",
                ChatCompletionResponse.class);
        assertNull(reparsed.getRequestId());
    }

    // -----------------------------------------------------------------------
    // (4) error path exposes request id
    // -----------------------------------------------------------------------

    @Test
    void errorResponse_exposesRequestId_fromEnvelope() {
        respondWith(401, "application/json", "req_hdr",
                "{\"error\":{\"code\":\"unauthorized\",\"message\":\"bad key\"},\"request_id\":\"req_env\"}");
        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> client.chat().completions().create(chatRequest()));
        assertEquals(401, err.getStatus());
        assertEquals("req_env", err.getRequestId());
    }

    @Test
    void errorResponse_exposesRequestId_fromHeaderWhenEnvelopeLacksIt() {
        respondWith(500, "application/json", "req_hdr_only",
                "{\"error\":{\"code\":\"internal_error\",\"message\":\"boom\"}}");
        MeshAPIError err = assertThrows(MeshAPIError.class,
                () -> client.chat().completions().create(chatRequest()));
        assertEquals("req_hdr_only", err.getRequestId());
    }
}
