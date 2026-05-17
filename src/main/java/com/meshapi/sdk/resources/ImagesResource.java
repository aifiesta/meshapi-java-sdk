package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.images.ImageGenerationChunk;
import com.meshapi.sdk.types.images.ImageGenerationRequest;
import com.meshapi.sdk.types.images.ImageGenerationResponse;

import java.util.Iterator;

public class ImagesResource {
    private final HttpClient http;

    public ImagesResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Sends a non-streaming image generation request and returns the full response.
     */
    public ImageGenerationResponse generate(ImageGenerationRequest params) {
        return http.post("/v1/images/generations", params, ImageGenerationResponse.class);
    }

    /**
     * Opens a streaming image generation request.
     *
     * <p>Returns an {@link Iterator} of SSE chunks. Iterate until {@link Iterator#hasNext()}
     * returns false. A mid-stream error causes {@link com.meshapi.sdk.MeshAPIError}
     * to be thrown from {@link Iterator#next()}.
     *
     * <p><strong>Streams do not retry.</strong> Catch the error and restart a new
     * {@link #stream} call if reconnection is needed.
     */
    public Iterator<ImageGenerationChunk> stream(ImageGenerationRequest params) {
        params.stream = true;
        return http.streamJson("/v1/images/generations", params, ImageGenerationChunk.class);
    }
}
