package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkEmbedRequest {
    @JsonProperty("file_ids") private List<String> fileIds;
    @JsonProperty("wait") private Boolean wait;
    @JsonProperty("metadata") private Map<String, Object> metadata;

    private BulkEmbedRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final BulkEmbedRequest req = new BulkEmbedRequest();

        public Builder fileIds(List<String> fileIds) { req.fileIds = fileIds; return this; }
        public Builder wait(Boolean wait) { req.wait = wait; return this; }
        public Builder metadata(Map<String, Object> metadata) { req.metadata = metadata; return this; }

        public BulkEmbedRequest build() { return req; }
    }
}
