package com.meshapi.sdk.types.keys;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Introspection details for a single API key, returned by {@code GET /v1/keys/{key_id}}.
 *
 * <p>The response never contains the key secret or its hash. Unknown fields are ignored so
 * that additive server changes do not break deserialisation.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiKeyDetails {

    /** The key's id. */
    @JsonProperty("id")
    public String id;

    /** The organisation that owns the key. */
    @JsonProperty("org_id")
    public String orgId;

    /** Human-readable label for the key, if one was set. */
    @JsonProperty("label")
    public String label;

    /** Masked form of the key, safe to display. */
    @JsonProperty("masked_key")
    public String maskedKey;

    /** Scopes granted to the key. */
    @JsonProperty("scopes")
    public List<String> scopes;

    /** Model ids the key may call, or {@code null} when unrestricted. */
    @JsonProperty("allowed_models")
    public List<String> allowedModels;

    /** Model ids the key may not call. */
    @JsonProperty("blocked_models")
    public List<String> blockedModels;

    /** Per-model limits configured on the key, as returned by the server. */
    @JsonProperty("model_limits")
    public JsonNode modelLimits;

    /** The key's effective limits after applying every tier, as returned by the server. */
    @JsonProperty("effective_limits")
    public JsonNode effectiveLimits;
}
