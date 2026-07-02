package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.documents.DocumentListResponse;
import com.meshapi.sdk.types.documents.DocumentResponse;
import com.meshapi.sdk.types.documents.GenerateDocumentRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Resource for the Documents endpoints.
 *
 * <pre>{@code
 * // Generate a document
 * DocumentResponse doc = client.documents().generate(
 *     GenerateDocumentRequest.builder()
 *         .format("pdf")
 *         .prompt("Write a summary of Q3 2025 sales results.")
 *         .build()
 * );
 *
 * // List documents
 * DocumentListResponse page = client.documents().list(20, 0);
 *
 * // Retrieve a document
 * DocumentResponse doc = client.documents().retrieve("doc_abc123");
 * }</pre>
 */
public class DocumentsResource {
    private final HttpClient http;

    public DocumentsResource(HttpClient http) {
        this.http = http;
    }

    /**
     * Generate a new document (POST /v1/documents/generate).
     *
     * @param params document generation parameters
     * @return the created DocumentResponse
     */
    public DocumentResponse generate(GenerateDocumentRequest params) {
        return http.post("/v1/documents/generate", params, DocumentResponse.class);
    }

    /**
     * List documents (GET /v1/documents) with default pagination.
     *
     * @return a page of documents
     */
    public DocumentListResponse list() {
        return list(null, null);
    }

    /**
     * List documents (GET /v1/documents) with pagination.
     *
     * @param limit  max number of results (1–200); null uses server default (50)
     * @param offset zero-based offset; null uses server default (0)
     * @return a page of documents
     */
    public DocumentListResponse list(Integer limit, Integer offset) {
        StringBuilder qs = new StringBuilder();
        if (limit != null) append(qs, "limit", String.valueOf(limit));
        if (offset != null) append(qs, "offset", String.valueOf(offset));
        return http.get("/v1/documents", qs.length() > 0 ? qs.toString() : null, DocumentListResponse.class);
    }

    /**
     * Retrieve a single document by ID (GET /v1/documents/{document_id}).
     *
     * @param documentId the document ID
     * @return the DocumentResponse
     */
    public DocumentResponse retrieve(String documentId) {
        String encoded = URLEncoder.encode(documentId, StandardCharsets.UTF_8).replace("+", "%20");
        return http.get("/v1/documents/" + encoded, DocumentResponse.class);
    }

    private static void append(StringBuilder sb, String key, String value) {
        if (sb.length() > 0) sb.append("&");
        sb.append(key).append("=").append(value);
    }
}
