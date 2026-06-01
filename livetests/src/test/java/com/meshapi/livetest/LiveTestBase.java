package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class LiveTestBase {

    private static final Map<String, String> SHARED_ENV = loadSharedEnv();
    protected static final String BASE_URL =
            envOrShared("MESHAPI_BASE_URL", "http://localhost:8000");
    protected static final String TOKEN =
            envOrShared("MESHAPI_TOKEN", "rsk_01KN96KQWDPF2X1E9CP8567JY4");
    protected static final String MODEL =
            envOrShared("MESHAPI_MODEL", "openai/gpt-4o-mini");
    protected static final String SECOND_MODEL = resolveSecondModel();

    private static String resolveSecondModel() {
        String fallback = MODEL.equals("anthropic/claude-haiku-4-5")
                ? "openai/gpt-4o-mini"
                : "anthropic/claude-haiku-4-5";
        return envOrShared("MESHAPI_SECOND_MODEL", fallback);
    }

    protected static String envOrShared(String key, String fallback) {
        String fromEnv = System.getenv(key);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        String fromFile = SHARED_ENV.get(key);
        if (fromFile != null && !fromFile.isBlank()) {
            return fromFile;
        }
        return fallback;
    }

    private static Map<String, String> loadSharedEnv() {
        Map<String, String> values = new HashMap<>();
        Path envPath = Path.of("..", ".env.livetest");
        if (!Files.exists(envPath)) {
            return values;
        }

        try {
            for (String rawLine : Files.readAllLines(envPath)) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }
                String[] parts = line.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                value = value.replaceAll("^[\"']", "").replaceAll("[\"']$", "");
                if (!key.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (IOException ignored) {
            return values;
        }

        return values;
    }

    protected static MeshAPI newClient() {
        return MeshAPI.builder()
                .baseUrl(BASE_URL)
                .token(TOKEN)
                .build();
    }

    protected static MeshAPI badClient() {
        return MeshAPI.builder()
                .baseUrl(BASE_URL)
                .token("rsk_00000000000000000000000000")
                .maxRetries(0)
                .build();
    }

    protected static String uniqueName(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    protected static boolean hasEnvOrShared(String key) {
        String value = envOrShared(key, "");
        return value != null && !value.isBlank();
    }

    protected static final String REALTIME_MODEL = envOrShared("MESHAPI_REALTIME_MODEL", "openai/gpt-realtime-mini");
}
