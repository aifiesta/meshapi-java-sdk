package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRequest {
    @JsonProperty("query") private String query;
    @JsonProperty("top_k") private Integer topK;
    @JsonProperty("file_ids") private List<String> fileIds;
    @JsonProperty("filter") private Map<String, Object> filter;
    @JsonProperty("date_from") private Long dateFrom;
    @JsonProperty("date_to") private Long dateTo;

    private SearchRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final SearchRequest req = new SearchRequest();

        public Builder query(String query) { req.query = query; return this; }
        public Builder topK(Integer topK) { req.topK = topK; return this; }
        public Builder fileIds(List<String> fileIds) { req.fileIds = fileIds; return this; }
        public Builder filter(Map<String, Object> filter) { req.filter = filter; return this; }
        public Builder dateFrom(Long dateFrom) { req.dateFrom = dateFrom; return this; }
        public Builder dateTo(Long dateTo) { req.dateTo = dateTo; return this; }

        public SearchRequest build() { return req; }
    }
}
