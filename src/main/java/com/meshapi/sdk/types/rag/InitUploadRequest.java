package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitUploadRequest {
    @JsonProperty("file_name") private String fileName;
    @JsonProperty("mime_type") private String mimeType;
    @JsonProperty("embed") private Boolean embed;
    @JsonProperty("metadata") private Map<String, Object> metadata;

    private InitUploadRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final InitUploadRequest req = new InitUploadRequest();

        public Builder fileName(String fileName) { req.fileName = fileName; return this; }
        public Builder mimeType(String mimeType) { req.mimeType = mimeType; return this; }
        public Builder embed(Boolean embed) { req.embed = embed; return this; }
        public Builder metadata(Map<String, Object> metadata) { req.metadata = metadata; return this; }

        public InitUploadRequest build() { return req; }
    }
}
