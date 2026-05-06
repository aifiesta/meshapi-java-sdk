package com.meshapi.sdk.types.embeddings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddingsRequest {
    @JsonProperty("model") public String model;
    @JsonProperty("input") public Object input;
    @JsonProperty("dimensions") public Integer dimensions;
    @JsonProperty("encoding_format") public String encodingFormat;
    @JsonProperty("input_type") public String inputType;
    @JsonProperty("provider") public Object provider;
    @JsonProperty("user") public String user;
}
