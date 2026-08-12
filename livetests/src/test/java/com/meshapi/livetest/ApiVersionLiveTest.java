package com.meshapi.livetest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshapi.sdk.MeshAPI;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Live checks that the version this SDK pins is one the gateway actually serves.
 *
 * <p>The unit tests prove the header is <i>sent</i>. Only a real gateway can prove it is
 * <b>accepted</b> — and that is the failure that matters: MeshAPI answers a version it
 * does not serve with {@code 400 invalid_api_version} rather than falling back, so a
 * stale {@link MeshAPI#API_VERSION} in a published release breaks every request that
 * release makes.
 */
@DisplayName("api version")
class ApiVersionLiveTest extends LiveTestBase {

    private static final String HEADER = "X-Mesh-Version";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final java.net.http.HttpClient RAW = java.net.http.HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .version(java.net.http.HttpClient.Version.HTTP_1_1)
            .build();

    /** A plain authenticated GET, bypassing the SDK so raw headers and status show. */
    private static HttpResponse<String> get(String path, String pin) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Authorization", "Bearer " + TOKEN)
                .timeout(Duration.ofSeconds(60));
        if (pin != null) {
            builder.header(HEADER, pin);
        }
        return RAW.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * The gateway's own list of pinnable versions, or null if this deployment predates
     * the endpoint.
     *
     * <p>{@code GET /v1/api-versions} landed in routersvc #1119 and reaches a deployment
     * only on a {@code v*.*.*} tag, so a 404 means "older than the endpoint" — not a
     * failure of this SDK.
     */
    private static JsonNode servedVersions() throws Exception {
        HttpResponse<String> response = get("/v1/api-versions", null);
        if (response.statusCode() == 404) {
            return null;
        }
        assertEquals(200, response.statusCode(), "GET /v1/api-versions");
        return MAPPER.readTree(response.body());
    }

    /**
     * The version the gateway says it served.
     *
     * <p>Read under either name on purpose. routersvc renamed the header to
     * {@code X-Mesh-Version} (#1110), but that reaches a deployment only on a tag — as of
     * 2026-08-12 api-dev echoes {@code x-mesh-version} and prod still echoes
     * {@code mesh-version}. Both accept either name on the <i>request</i>, so this SDK's
     * pin is honoured on both; only the echo lags. Being strict about the echo name here
     * would report a green SDK as broken against an untagged prod.
     */
    private static String echoed(HttpResponse<String> response) {
        return response.headers().firstValue(HEADER)
                .or(() -> response.headers().firstValue("Mesh-Version"))
                .orElse(null);
    }

    @Test
    @DisplayName("the pinned version is served")
    void pinnedVersionIsServed() {
        // The whole point: a real request carrying this SDK's pin must succeed. A failure
        // with invalid_api_version means the constant is stale and this release cannot
        // talk to the gateway at all.
        assertFalse(newClient().models().list(null).isEmpty(), "expected at least one model");
    }

    @Test
    @DisplayName("the gateway lists our pinned version")
    void gatewayListsOurPin() throws Exception {
        // Catches a stale SDK the moment a version is retired, rather than when a
        // customer reports a 400.
        JsonNode versions = servedVersions();
        Assumptions.assumeTrue(versions != null,
                BASE_URL + " predates GET /v1/api-versions (routersvc #1119)");

        List<String> labels = new ArrayList<>();
        versions.forEach(entry -> labels.add(entry.get("label").asText()));
        assertTrue(labels.contains(MeshAPI.API_VERSION),
                "this SDK pins " + MeshAPI.API_VERSION + ", which " + BASE_URL
                        + " does not serve; served: " + labels);
    }

    @Test
    @DisplayName("our pinned version is not already sunset")
    void ourPinIsNotSunset() throws Exception {
        // A version can still be listed while on its way out. A release pinning a sunset
        // version is already broken, it just has not failed yet.
        JsonNode versions = servedVersions();
        Assumptions.assumeTrue(versions != null, "endpoint not deployed");

        for (JsonNode entry : versions) {
            if (MeshAPI.API_VERSION.equals(entry.get("label").asText())) {
                assertNotEquals("sunset", entry.get("status").asText(),
                        "this SDK pins a sunset version; sunset_on=" + entry.get("sunset_on"));
            }
        }
    }

    @Test
    @DisplayName("the response echoes the version served")
    void responseEchoesTheVersionServed() throws Exception {
        HttpResponse<String> response = get("/v1/models", MeshAPI.API_VERSION);
        assertEquals(200, response.statusCode(), "GET /v1/models");

        assertEquals(MeshAPI.API_VERSION, echoed(response));
    }

    @Test
    @DisplayName("an unserved version is rejected loudly")
    void unservedVersionIsRejectedLoudly() throws Exception {
        // Confirms the gateway does NOT silently fall back — the property the whole
        // pinning scheme rests on. If this returned 200, a typo'd pin would leave a
        // caller believing they were pinned when they were not.
        HttpResponse<String> response = get("/v1/models", "1999-01");

        assertEquals(400, response.statusCode(), "an unserved version must be rejected");
        assertEquals("invalid_api_version",
                MAPPER.readTree(response.body()).get("error").get("code").asText());
    }

    @Test
    @DisplayName("an unpinned request is served the baseline")
    void unpinnedRequestGetsTheBaseline() throws Exception {
        // No header means the gateway's baseline, and it says which one it used. This is
        // what apiVersion(null) opts into.
        JsonNode versions = servedVersions();
        Assumptions.assumeTrue(versions != null, "endpoint not deployed");

        String baseline = null;
        for (JsonNode entry : versions) {
            if (entry.get("baseline").asBoolean()) {
                baseline = entry.get("label").asText();
            }
        }
        assertNotNull(baseline, "the gateway must mark exactly one version as the baseline");

        HttpResponse<String> response = get("/v1/models", null);
        assertEquals(200, response.statusCode(), "GET /v1/models");
        assertEquals(baseline, echoed(response));
    }
}
