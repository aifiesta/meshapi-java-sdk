package com.meshapi.sdk.resources;

import com.meshapi.sdk.types.keys.ApiKeyDetails;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * API key introspection.
 *
 * <p>Example:</p>
 * <pre>{@code
 * ApiKeyDetails key = client.keys().retrieve("key_123");
 * System.out.println(key.scopes);
 * }</pre>
 */
public class KeysResource {

    /** Minimal transport used by this resource to issue GET requests. */
    @FunctionalInterface
    public interface Getter {
        /**
         * Sends a GET request to {@code path} and deserialises the body into {@code type}.
         */
        <T> T get(String path, Class<T> type);
    }

    private final Getter http;

    /**
     * Creates the resource over the given transport.
     *
     * @param http transport for GET requests
     */
    public KeysResource(Getter http) {
        this.http = Objects.requireNonNull(http, "http");
    }

    /**
     * Retrieves details for a single API key via {@code GET /v1/keys/{key_id}}.
     *
     * <p>With a data-plane key ({@code rsk_...}) only the caller's own key can be read; any other
     * id returns 404. An admin key or user session needs the {@code keys:read} scope; a key it
     * can see but not read returns 403.</p>
     *
     * @param keyId the key id
     * @return the key's details (never the secret)
     * @throws IllegalArgumentException if {@code keyId} is null or blank
     */
    public ApiKeyDetails retrieve(String keyId) {
        if (keyId == null || keyId.isBlank()) {
            throw new IllegalArgumentException("keyId must not be blank");
        }
        String encoded = URLEncoder.encode(keyId, StandardCharsets.UTF_8).replace("+", "%20");
        return http.get("/v1/keys/" + encoded, ApiKeyDetails.class);
    }
}
