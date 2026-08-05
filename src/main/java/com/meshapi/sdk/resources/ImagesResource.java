package com.meshapi.sdk.resources;

import com.meshapi.sdk.RequestOptions;
import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.images.ImageEditRequest;
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
        return generate(params, null);
    }

    /** Non-streaming image generation with per-request options (e.g. {@code X-Request-Id}). */
    public ImageGenerationResponse generate(ImageGenerationRequest params, RequestOptions options) {
        return http.post("/v1/images/generations", params, ImageGenerationResponse.class, options);
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
     *
     * <p>This method does NOT mutate the caller's {@code params} object.
     */
    public Iterator<ImageGenerationChunk> stream(ImageGenerationRequest params) {
        return stream(params, (RequestOptions) null);
    }

    /**
     * Streaming image generation with per-request options
     * (e.g. {@code X-Request-Id}).
     *
     * @see #stream(ImageGenerationRequest)
     */
    public Iterator<ImageGenerationChunk> stream(ImageGenerationRequest params, RequestOptions options) {
        // Build a copy of the request with stream=true rather than mutating caller's object.
        ImageGenerationRequest streamReq = ImageGenerationRequest.builder()
                .prompt(params.prompt)
                .model(params.model)
                .n(params.n)
                .size(params.size)
                .quality(params.quality)
                .responseFormat(params.responseFormat)
                .outputFormat(params.outputFormat)
                .aspectRatio(params.aspectRatio)
                .resolution(params.resolution)
                .outputCompression(params.outputCompression)
                .background(params.background)
                .moderation(params.moderation)
                .partialImages(params.partialImages)
                .image(params.image)
                .seed(params.seed)
                .sequentialImageGeneration(params.sequentialImageGeneration)
                .sequentialImageGenerationOptions(params.sequentialImageGenerationOptions)
                .guidanceScale(params.guidanceScale)
                .watermark(params.watermark)
                .optimizePromptOptions(params.optimizePromptOptions)
                .stream(true)
                .build();
        return http.streamJson("/v1/images/generations", streamReq, ImageGenerationChunk.class, options);
    }

    /**
     * Edit an existing image (POST /v1/images/edits).
     *
     * <p>Accepts a JSON request body (not multipart). The response reuses
     * {@link ImageGenerationResponse}.
     *
     * <pre>{@code
     * ImageGenerationResponse resp = client.images().edit(
     *     ImageEditRequest.builder()
     *         .model("openai/gpt-image-1")
     *         .image("data:image/png;base64,...")
     *         .prompt("Make the sky blue")
     *         .build()
     * );
     * }</pre>
     */
    public ImageGenerationResponse edit(ImageEditRequest params) {
        return edit(params, null);
    }

    /** Image edit with per-request options (e.g. {@code X-Request-Id}). */
    public ImageGenerationResponse edit(ImageEditRequest params, RequestOptions options) {
        return http.post("/v1/images/edits", params, ImageGenerationResponse.class, options);
    }
}
