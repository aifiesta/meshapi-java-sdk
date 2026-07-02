package com.meshapi.sdk.types.moderations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A single moderation result within a {@link ModerationResponse}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationResult {
    /** Whether the content was flagged as potentially harmful. */
    @JsonProperty("flagged") public boolean flagged;

    /** Category-to-flagged mapping (e.g. {@code {"hate": false, "violence": true}}). */
    @JsonProperty("categories") public Map<String, Boolean> categories;

    /** Category-to-confidence-score mapping (values 0.0–1.0). */
    @JsonProperty("category_scores") public Map<String, Double> categoryScores;
}
