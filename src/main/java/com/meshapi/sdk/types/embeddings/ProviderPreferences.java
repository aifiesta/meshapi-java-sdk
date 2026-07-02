package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Provider routing preferences for embedding (and other) requests.
 *
 * <p>Can be passed as the {@code provider} field on {@link EmbeddingsRequest}
 * (in addition to a plain provider-name string).
 *
 * <pre>{@code
 * ProviderPreferences prefs = new ProviderPreferences();
 * prefs.order = List.of("openai", "cohere");
 * prefs.allowFallbacks = true;
 *
 * EmbeddingsRequest req = new EmbeddingsRequest();
 * req.provider = prefs;
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderPreferences {
    /** Ordered list of provider names to prefer. */
    @JsonProperty("order") public List<String> order;

    /** Whether to fall back to the next provider when the primary fails. */
    @JsonProperty("allow_fallbacks") public Boolean allowFallbacks;

    /** Whether all requested parameters must be supported by the chosen provider. */
    @JsonProperty("require_parameters") public Boolean requireParameters;

    /**
     * Data-collection policy.
     * Valid values: {@code "allow"}, {@code "deny"}.
     */
    @JsonProperty("data_collection") public String dataCollection;
}
