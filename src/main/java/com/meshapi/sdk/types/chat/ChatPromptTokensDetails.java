package com.meshapi.sdk.types.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Breakdown of prompt tokens in a chat completion's {@code usage}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatPromptTokensDetails {

    /** Prompt tokens served from the provider's prompt cache. */
    @JsonProperty("cached_tokens")
    public Integer cachedTokens;

    /** Prompt tokens that were audio input. */
    @JsonProperty("audio_tokens")
    public Integer audioTokens;

    /**
     * Prompt tokens written to the provider's cache with a 12-hour TTL.
     * {@code 0} when no 12-hour cache write occurred; {@code null} when not reported.
     */
    @JsonProperty("cache_write_12h_tokens")
    public Integer cacheWrite12hTokens;
}
