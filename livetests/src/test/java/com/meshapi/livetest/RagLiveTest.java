package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.types.rag.BulkEmbedRequest;
import com.meshapi.sdk.types.rag.BulkEmbedResponse;
import com.meshapi.sdk.types.rag.InitUploadRequest;
import com.meshapi.sdk.types.rag.InitUploadResponse;
import com.meshapi.sdk.types.rag.RagFileListResponse;
import com.meshapi.sdk.types.rag.RagFileStatus;
import com.meshapi.sdk.types.rag.SearchRequest;
import com.meshapi.sdk.types.rag.SearchResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RagLiveTest extends LiveTestBase {

    /** Document uploaded in every RAG live test — contains a unique searchable phrase. */
    private static final String RAG_TEST_CONTENT =
            "MeshAPI SDK live test document.\n" +
            "This file is used to verify RAG upload, embedding, and vector search.\n" +
            "The document contains the unique phrase \"meshapi rag livetest java\" " +
            "so search results are deterministic.\n";
    private static final String MIME_TYPE = "text/plain";
    private static final long MAX_EMBED_WAIT_MS = 90_000;

    /** PUT raw bytes to a signed URL using the JDK HTTP client. */
    private static void putFile(String signedUrl, String mimeType, String content)
            throws IOException, InterruptedException {
        HttpClient http = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(signedUrl))
                .header("Content-Type", mimeType)
                .PUT(HttpRequest.BodyPublishers.ofString(content, StandardCharsets.UTF_8))
                .build();
        HttpResponse<Void> resp = http.send(req, HttpResponse.BodyHandlers.discarding());
        assertTrue(resp.statusCode() < 400,
                "PUT signed URL returned HTTP " + resp.statusCode());
    }

    /** Poll until embedding_status reaches "ready"; fail on "failed" or timeout. */
    private static void pollEmbedding(MeshAPI client, String fileId) throws InterruptedException {
        long deadline = System.currentTimeMillis() + MAX_EMBED_WAIT_MS;
        while (System.currentTimeMillis() < deadline) {
            RagFileStatus s = client.rag().get(fileId);
            System.out.printf("  polling embedding_status=%s for file %s%n", s.embeddingStatus, fileId);
            if ("ready".equals(s.embeddingStatus)) return;
            if ("failed".equals(s.embeddingStatus)) {
                fail("embedding failed for " + fileId + ": error_code=" + s.lastErrorCode);
            }
            Thread.sleep(3_000);
        }
        fail("embedding did not reach 'ready' within " + MAX_EMBED_WAIT_MS + "ms for " + fileId);
    }

    /**
     * Paginate through all RAG files until fileId is found or the list is exhausted.
     * Returns true if found.
     */
    private static boolean findFileInList(MeshAPI client, String fileId) {
        final int pageSize = 50;
        int offset = 0;
        while (true) {
            RagFileListResponse page = client.rag().list(pageSize, offset);
            for (RagFileStatus f : page.files) {
                if (fileId.equals(f.fileId)) return true;
            }
            offset += page.files.size();
            if (offset >= page.total) return false;
        }
    }

    @Test
    void uploadEmbedSearch() throws IOException, InterruptedException {
        MeshAPI client = newClient();
        String fileName = "java-livetest-" + System.currentTimeMillis() + ".txt";

        // ── Step 1: InitUpload (embed=false to test the embed endpoint explicitly) ──
        InitUploadResponse upload = client.rag().initUpload(
                InitUploadRequest.builder()
                        .fileName(fileName)
                        .mimeType(MIME_TYPE)
                        .embed(false)
                        .build());
        assertNotNull(upload.fileId, "expected file_id");
        assertNotNull(upload.signedUrl, "expected signed_url");
        System.out.printf("[PASS] rag.initUpload → file_id=%s%n", upload.fileId);

        // Note: the RAG API has no DELETE endpoint, so uploaded files cannot be
        // cleaned up programmatically. Each test run leaves one file in the account.

        // ── Step 2: PUT file content to signed URL ──
        putFile(upload.signedUrl, MIME_TYPE, RAG_TEST_CONTENT);
        System.out.println("[PASS] PUT file content to signed URL");

        // ── Step 3: Poll until upload_status=ready ──
        long uploadDeadline = System.currentTimeMillis() + 30_000;
        RagFileStatus uploadStatus = null;
        while (System.currentTimeMillis() < uploadDeadline) {
            RagFileStatus s = client.rag().get(upload.fileId);
            if ("ready".equals(s.uploadStatus)) {
                uploadStatus = s;
                break;
            }
            if ("failed".equals(s.uploadStatus)) {
                fail("upload failed for " + upload.fileId + ": error_code=" + s.lastErrorCode);
            }
            Thread.sleep(2_000);
        }
        assertNotNull(uploadStatus, "upload_status did not reach 'ready' within 30s");
        System.out.printf("[PASS] rag.get → upload_status=%s embedding_status=%s%n",
                uploadStatus.uploadStatus, uploadStatus.embeddingStatus);

        // ── Step 4: Embed ──
        BulkEmbedResponse embedResp = client.rag().embed(
                BulkEmbedRequest.builder()
                        .fileIds(List.of(upload.fileId))
                        .build());
        assertFalse(embedResp.results.isEmpty(), "embed returned no results");
        System.out.printf("[PASS] rag.embed → status=%s%n", embedResp.results.get(0).embeddingStatus);

        // ── Step 5: Poll until embedding_status=ready ──
        pollEmbedding(client, upload.fileId);
        System.out.printf("[PASS] embedding complete for %s%n", upload.fileId);

        // ── Step 6: List — paginate until file is found or all pages exhausted ──
        assertTrue(findFileInList(client, upload.fileId), "uploaded file not found in list");
        System.out.println("[PASS] rag.list → uploaded file present");

        // ── Step 7: Search ──
        SearchResponse searchResp = client.rag().search(
                SearchRequest.builder()
                        .query("meshapi rag livetest java")
                        .topK(5)
                        .fileIds(List.of(upload.fileId))
                        .build());
        assertFalse(searchResp.results.isEmpty(), "search returned no results");
        System.out.printf("[PASS] rag.search → %d results, top score=%.4f%n",
                searchResp.results.size(), searchResp.results.get(0).score);
    }
}
