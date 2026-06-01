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

public class RagResource {

    private final HttpClient http;

    public RagResource(HttpClient http) {
        this.http = http;
    }

    /** Initialise a RAG file upload and return a signed URL for the file content. */
    public InitUploadResponse initUpload(InitUploadRequest params) {
        return http.post("/v1/files", params, InitUploadResponse.class);
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
