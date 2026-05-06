package com.meshapi.sdk.resources;

import com.meshapi.sdk.internal.HttpClient;
import com.meshapi.sdk.types.batch.FileObject;
import com.meshapi.sdk.types.batch.UploadBatchFileRequest;

public class FilesResource {
    private final HttpClient http;

    public FilesResource(HttpClient http) {
        this.http = http;
    }

    public FileObject upload(UploadBatchFileRequest params) {
        return http.post("/v1/files", params, FileObject.class);
    }

    public FileObject get(String fileId) {
        return http.get("/v1/files/" + fileId, FileObject.class);
    }

    public void delete(String fileId) {
        http.delete("/v1/files/" + fileId);
    }

    public byte[] content(String fileId) {
        return http.getBytes("/v1/files/" + fileId + "/content");
    }
}
