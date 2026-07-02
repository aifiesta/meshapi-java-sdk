package com.meshapi.sdk.types.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Request body for POST /v1/documents/generate.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateDocumentRequest {
    /**
     * Required. Output format: {@code pdf}, {@code docx}, {@code pptx}, {@code csv}, or {@code xlsx}.
     */
    @JsonProperty("format") private String format;

    /** Required. The prompt used to generate the document (1–50 000 characters). */
    @JsonProperty("prompt") private String prompt;

    /** Optional. Model to use; server defaults to {@code "google/gemini-2.5-flash-lite"}. */
    @JsonProperty("model") private String model;

    /** Optional. Arbitrary metadata to attach to the document. */
    @JsonProperty("metadata") private Map<String, Object> metadata;

    private GenerateDocumentRequest() {}

    public String getFormat() { return format; }
    public String getPrompt() { return prompt; }
    public String getModel() { return model; }
    public Map<String, Object> getMetadata() { return metadata; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final GenerateDocumentRequest req = new GenerateDocumentRequest();

        public Builder format(String format) { req.format = format; return this; }
        public Builder prompt(String prompt) { req.prompt = prompt; return this; }
        public Builder model(String model) { req.model = model; return this; }
        public Builder metadata(Map<String, Object> metadata) { req.metadata = metadata; return this; }

        public GenerateDocumentRequest build() {
            if (req.format == null || req.format.isBlank()) throw new IllegalStateException("format is required");
            if (req.prompt == null || req.prompt.isBlank()) throw new IllegalStateException("prompt is required");
            return req;
        }
    }
}
