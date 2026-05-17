package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.images.ImageGenerationRequest;
import com.meshapi.sdk.types.images.ImageGenerationResponse;

public class ImagesResource {
    private final HttpClient http;

    public ImagesResource(HttpClient http) {
        this.http = http;
    }

    public ImageGenerationResponse generate(ImageGenerationRequest params) {
        return http.post("/v1/images/generations", params, ImageGenerationResponse.class);
    }
}
