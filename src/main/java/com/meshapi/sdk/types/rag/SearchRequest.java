package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request body for {@code POST /v1/files/search}.
 *
 * <p>Build instances with {@link #builder()}. Unset fields are omitted from the JSON body.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SearchRequest {

    /** Retrieval mode {@code vector}. */
    public static final String MODE_VECTOR = "vector";
    /** Retrieval mode {@code graph}. */
    public static final String MODE_GRAPH = "graph";
    /** Retrieval mode {@code agentic}. */
    public static final String MODE_AGENTIC = "agentic";

    @JsonProperty("query")
    private final String query;

    @JsonProperty("file_ids")
    private final List<String> fileIds;

    @JsonProperty("top_k")
    private final Integer topK;

    @JsonProperty("mode")
    private final String mode;

    private SearchRequest(Builder b) {
        this.query = b.query;
        this.fileIds = b.fileIds == null ? null : List.copyOf(b.fileIds);
        this.topK = b.topK;
        this.mode = b.mode;
    }

    /** @return a new builder. */
    public static Builder builder() {
        return new Builder();
    }

    public String getQuery() { return query; }

    public List<String> getFileIds() { return fileIds; }

    public Integer getTopK() { return topK; }

    /** @return the retrieval mode, or {@code null} to use the server default. */
    public String getMode() { return mode; }

    /** Builder for {@link SearchRequest}. */
    public static final class Builder {
        private String query;
        private List<String> fileIds;
        private Integer topK;
        private String mode;

        private Builder() {}

        /** Search query text. */
        public Builder query(String query) { this.query = query; return this; }

        /** Restrict the search to these file ids. */
        public Builder fileIds(List<String> fileIds) { this.fileIds = fileIds; return this; }

        /** Maximum number of results to return. */
        public Builder topK(Integer topK) { this.topK = topK; return this; }

        /**
         * Retrieval mode, sent as {@code mode}. See {@link #MODE_VECTOR}, {@link #MODE_GRAPH}
         * and {@link #MODE_AGENTIC}. A mode that is not enabled for your account is refused
         * with a 400 error.
         *
         * @param mode retrieval mode, or {@code null} to use the server default
         */
        public Builder mode(String mode) { this.mode = mode; return this; }

        public SearchRequest build() {
            return new SearchRequest(this);
        }
    }
}
