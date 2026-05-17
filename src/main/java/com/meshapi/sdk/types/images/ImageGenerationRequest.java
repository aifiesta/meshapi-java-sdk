package com.meshapi.sdk.types.images;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageGenerationRequest {
    @JsonProperty("prompt") public String prompt;
    @JsonProperty("model") public String model;
    @JsonProperty("n") public Integer n;
    @JsonProperty("size") public String size;
    @JsonProperty("quality") public String quality;
    @JsonProperty("response_format") public String responseFormat;
    @JsonProperty("output_format") public String outputFormat;
    @JsonProperty("stream") public Boolean stream;

    private ImageGenerationRequest() {}

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final ImageGenerationRequest req = new ImageGenerationRequest();

        public Builder prompt(String prompt) { req.prompt = prompt; return this; }
        public Builder model(String model) { req.model = model; return this; }
        public Builder n(Integer n) { req.n = n; return this; }
        public Builder size(String size) { req.size = size; return this; }
        public Builder quality(String quality) { req.quality = quality; return this; }
        public Builder responseFormat(String responseFormat) { req.responseFormat = responseFormat; return this; }
        public Builder outputFormat(String outputFormat) { req.outputFormat = outputFormat; return this; }
        public Builder stream(Boolean stream) { req.stream = stream; return this; }

        public ImageGenerationRequest build() {
            return req;
        }
    }
}
