package com.meshapi.sdk.unit;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.audio.TranscriptionRequest;
import com.meshapi.sdk.types.templates.CreateTemplateRequest;
import com.meshapi.sdk.types.templates.UpdateTemplateRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * X-Mesh-Version — the dated API version this SDK was built against (MESH-508).
 *
 * <p>MeshAPI versions its contract by date, in a request header. An SDK that sends
 * nothing is served the gateway's BASELINE — safe today, but it also means the SDK
 * never states which response shape it can actually parse. It works today only
 * because BASELINE is the OLDEST supported version, so it never moves on its own.
 * Pinning turns that from a coincidence into a contract, and puts this release into
 * {@code usage_events.api_version} so a version can be retired on evidence.
 *
 * <p>Why this test uses a real loopback server rather than a mock: {@code HttpClient}
 * builds its request headers at <b>eight separate</b> {@code HttpRequest.newBuilder()}
 * sites — get, delete, stream (x2), getBytes, postBytes, postMultipart, and the shared
 * jsonRequest for POST/PATCH. A header added to one and missed at another is exactly
 * the failure this file exists to prevent, so every one of those call shapes is
 * exercised against a server that records what actually arrived.
 *
 * <p>Uses the JDK's own {@code com.sun.net.httpserver}, so it adds no dependency.
 */
@DisplayName("X-Mesh-Version request header")
class ApiVersionHeaderTest {

    private static final String HEADER = "X-Mesh-Version";

    private HttpServer server;
    private final List<Map<String, List<String>>> seen =
            Collections.synchronizedList(new ArrayList<>());

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            seen.add(exchange.getRequestHeaders());
            respond(exchange, body(exchange.getRequestURI().getPath()));
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    /** A body each endpoint under test can deserialise into its response type. */
    private static String body(String path) {
        if (path.startsWith("/v1/models")) return "[]";
        if (path.startsWith("/v1/audio/transcriptions")) return "{\"text\":\"ok\"}";
        if (path.startsWith("/v1/templates")) return "{\"id\":\"tpl_1\",\"name\":\"t\"}";
        return "{}";
    }

    private static void respond(HttpExchange exchange, String payload) throws IOException {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    /** The version header from the most recent request, or null if it was absent. */
    private String lastPin() {
        assertFalse(seen.isEmpty(), "the server recorded no request");
        List<String> values = seen.get(seen.size() - 1).get(HEADER);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private MeshAPI client() {
        return MeshAPI.builder().baseUrl(baseUrl()).token("rsk_test").build();
    }

    // -- the constant ------------------------------------------------------------

    @Test
    @DisplayName("is exposed as a dated label")
    void constantIsADatedLabel() {
        // Public because a caller pinning explicitly needs a value to pass, and a
        // caller debugging a shape mismatch needs to know what was sent.
        assertEquals("2026-08", MeshAPI.API_VERSION);
    }

    @Test
    @DisplayName("is a well-formed YYYY-MM")
    void constantIsWellFormed() {
        // The gateway 400s a malformed label rather than falling back, so a typo here
        // would break every request this SDK makes, not degrade quietly.
        Matcher m = Pattern.compile("^(\\d{4})-(\\d{2})$").matcher(MeshAPI.API_VERSION);
        assertTrue(m.matches(), MeshAPI.API_VERSION + " is not YYYY-MM");
        int month = Integer.parseInt(m.group(2));
        assertTrue(month >= 1 && month <= 12, "month " + month + " out of range");
    }

    @Test
    @DisplayName("is distinct from the SDK identity header")
    void doesNotDisplaceTheSdkIdentityHeader() {
        // Two headers with two different jobs: one says which SDK build, the other
        // which contract. Neither substitutes for the other.
        client().models().list(null);

        List<String> sdk = seen.get(seen.size() - 1).get("X-MeshAPI-SDK");
        assertNotNull(sdk, "the SDK identity header went missing");
        assertTrue(sdk.get(0).startsWith("java/"));
        assertEquals(MeshAPI.API_VERSION, lastPin());
    }

    // -- every request-building site ---------------------------------------------

    @Test
    @DisplayName("is sent on GET")
    void sentOnGet() {
        client().models().list(null);
        assertEquals(MeshAPI.API_VERSION, lastPin());
    }

    @Test
    @DisplayName("is sent on POST")
    void sentOnPost() {
        client().templates().create(CreateTemplateRequest.builder().name("t").build());
        assertEquals(MeshAPI.API_VERSION, lastPin());
    }

    @Test
    @DisplayName("is sent on PATCH")
    void sentOnPatch() {
        // POST and PATCH share jsonRequest(), but the builder is completed differently
        // for each, so both are worth pinning down.
        client().templates().update("tpl_1", UpdateTemplateRequest.builder().name("t2").build());
        assertEquals(MeshAPI.API_VERSION, lastPin());
    }

    @Test
    @DisplayName("is sent on DELETE")
    void sentOnDelete() {
        client().templates().delete("tpl_1");
        assertEquals(MeshAPI.API_VERSION, lastPin());
    }

    @Test
    @DisplayName("is sent on multipart uploads")
    void sentOnMultipart() {
        // This site sets its own Content-Type with the multipart boundary, so it does
        // not share a builder with the JSON paths.
        client().audio().transcribe(
                new byte[] {1, 2, 3}, "a.mp3",
                TranscriptionRequest.builder().model("openai/whisper-1").build());
        assertEquals(MeshAPI.API_VERSION, lastPin());
    }

    // -- opting out --------------------------------------------------------------

    @Test
    @DisplayName("honours an explicit pin")
    void perClientOverride() {
        // A customer who has migrated ahead of this SDK release must not be forced
        // back onto the version the SDK was built against.
        MeshAPI.builder().baseUrl(baseUrl()).token("rsk_test").apiVersion("2026-09")
                .build().models().list(null);

        assertEquals("2026-09", lastPin());
    }

    @Test
    @DisplayName("sends nothing when set to null")
    void nullMeansSendNothing() {
        // Explicit opt-out, distinct from "never called". Omitting the header entirely
        // is how a caller asks for the gateway's baseline whatever it may become — the
        // pre-MESH-508 behaviour, still reachable on purpose.
        MeshAPI.builder().baseUrl(baseUrl()).token("rsk_test").apiVersion(null)
                .build().models().list(null);

        assertEquals(null, lastPin(), "the header was sent despite an explicit opt-out");
    }

    @Test
    @DisplayName("sends nothing rather than an empty value")
    void neverSendsAnEmptyValue() {
        // The gateway treats an EMPTY value as a typo'd pin and 400s it, rather than
        // reading it as "no pin". Sending the header with no value would therefore
        // break every request instead of falling back to the baseline.
        MeshAPI.builder().baseUrl(baseUrl()).token("rsk_test").apiVersion("")
                .build().models().list(null);

        assertEquals(null, lastPin(), "an empty pin must omit the header, not send it blank");
    }
}
