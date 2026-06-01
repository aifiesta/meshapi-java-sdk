package com.meshapi.sdk.types.rag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {
    @JsonProperty("results")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public List<SearchResult> results = new ArrayList<>();
}
