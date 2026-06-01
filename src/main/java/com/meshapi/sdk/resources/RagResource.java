package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.rag.BulkEmbedRequest;
import com.meshapi.sdk.types.rag.BulkEmbedResponse;
import com.meshapi.sdk.types.rag.InitUploadRequest;
import com.meshapi.sdk.types.rag.InitUploadResponse;
import com.meshapi.sdk.types.rag.RagFileListResponse;
import com.meshapi.sdk.types.rag.RagFileStatus;
import com.meshapi.sdk.types.rag.SearchRequest;
import com.meshapi.sdk.types.rag.SearchResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class RagResource {

    private final HttpClient http;

    public RagResource(HttpClient http) {
        this.http = http;
    }

    /** Initialise a RAG file upload and return a signed URL for the file content. */
    public InitUploadResponse initUpload(InitUploadRequest params) {
        return http.post("/v1/files", params, InitUploadResponse.class);
    }

    /**
     * Convenience wrapper: calls {@link #initUpload} then PUTs {@code content} to
     * the returned signed URL in one step. Returns the same {@link InitUploadResponse}
     * so the caller has the {@code fileId}.
     *
     * @param fileName file name (e.g. "report.pdf")
     * @param mimeType MIME type (e.g. "application/pdf")
     * @param content  raw file bytes
     * @param embed    whether to auto-trigger embedding after upload (nullable = server default)
     * @param metadata optional key/value metadata
     */
    public InitUploadResponse uploadFile(
            String fileName,
            String mimeType,
            byte[] content,
            Boolean embed,
            Map<String, Object> metadata) {

        InitUploadRequest req = InitUploadRequest.builder()
                .fileName(fileName)
                .mimeType(mimeType)
                .embed(embed)
                .metadata(metadata)
                .build();
        InitUploadResponse upload = initUpload(req);

        try {
            java.net.http.HttpClient jdkClient = java.net.http.HttpClient.newHttpClient();
            HttpRequest putReq = HttpRequest.newBuilder()
                    .uri(URI.create(upload.signedUrl))
                    .header("Content-Type", mimeType)
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(content))
                    .build();
            HttpResponse<Void> putResp = jdkClient.send(putReq, HttpResponse.BodyHandlers.discarding());
            if (putResp.statusCode() >= 400) {
                throw new RuntimeException("rag: PUT signed URL returned HTTP " + putResp.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("rag: PUT signed URL failed", e);
        }

        return upload;
    }

    /** List RAG files with optional pagination. */
    public RagFileListResponse list(Integer limit, Integer offset) {
        StringBuilder qs = new StringBuilder();
        if (limit != null) qs.append("limit=").append(limit);
        if (offset != null) {
            if (qs.length() > 0) qs.append("&");
            qs.append("offset=").append(offset);
        }
        if (qs.length() > 0) {
            return http.get("/v1/files", qs.toString(), RagFileListResponse.class);
        }
        return http.get("/v1/files", RagFileListResponse.class);
    }

    /** Get the status of a single RAG file. */
    public RagFileStatus get(String fileId) {
        return http.get("/v1/files/" + fileId, RagFileStatus.class);
    }

    /** Enqueue embedding jobs for one or more files. */
    public BulkEmbedResponse embed(BulkEmbedRequest params) {
        return http.post("/v1/files/embed", params, BulkEmbedResponse.class);
    }

    /** Perform a vector similarity search over embedded files. */
    public SearchResponse search(SearchRequest params) {
        return http.post("/v1/files/search", params, SearchResponse.class);
    }
}
