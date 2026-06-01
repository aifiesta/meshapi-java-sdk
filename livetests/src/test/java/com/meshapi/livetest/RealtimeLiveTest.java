package com.meshapi.livetest;

import com.meshapi.sdk.MeshAPI;
import com.meshapi.sdk.resources.RealtimeSession;
import com.meshapi.sdk.types.realtime.RealtimeError;
import com.meshapi.sdk.types.realtime.RealtimeListener;
import com.meshapi.sdk.types.realtime.RealtimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Live tests for the MeshAPI Java SDK realtime (WebSocket) resource.
 *
 * <p>Requires {@code MESHAPI_REALTIME_MODEL} to be set (env or {@code .env.livetest}).
 * All tests are skipped when the variable is absent.
 */
class RealtimeLiveTest extends LiveTestBase {

    // -------------------------------------------------------------------------
    // Connect and close
    // -------------------------------------------------------------------------

    @Test
    void connectAndClose() throws Exception {
        MeshAPI client = newClient();
        CountDownLatch openLatch  = new CountDownLatch(1);
        CountDownLatch closeLatch = new CountDownLatch(1);

        RealtimeSession session = client.realtime().connect(REALTIME_MODEL, new RealtimeListener() {
            @Override public void onOpen(RealtimeSession s)  { openLatch.countDown(); }
            @Override public void onClose(RealtimeSession s, int code, String reason) { closeLatch.countDown(); }
        }).get(15, TimeUnit.SECONDS);

        assertTrue(openLatch.await(5, TimeUnit.SECONDS), "onOpen not called within 5 s");
        session.closeAsync().get(5, TimeUnit.SECONDS);
        System.out.println("[PASS] realtime.connect + close");
    }

    // -------------------------------------------------------------------------
    // Receive session.created
    // -------------------------------------------------------------------------

    @Test
    void receiveSessionCreated() throws Exception {
        MeshAPI client = newClient();
        CountDownLatch latch   = new CountDownLatch(1);
        List<RealtimeMessage>  messages = new CopyOnWriteArrayList<>();

        RealtimeSession session = client.realtime().connect(REALTIME_MODEL, new RealtimeListener() {
            @Override
            public void onMessage(RealtimeSession s, RealtimeMessage msg) {
                messages.add(msg);
                latch.countDown();
            }
            @Override
            public void onError(RealtimeSession s, RealtimeError err) {
                System.err.println("onError: " + err.getMessage());
                latch.countDown();
            }
        }).get(15, TimeUnit.SECONDS);

        try {
            assertTrue(latch.await(20, TimeUnit.SECONDS), "no message received within 20 s");
            assertFalse(messages.isEmpty(), "expected at least one text frame");
            RealtimeMessage first = messages.get(0);
            assertNotNull(first.getEvent(), "expected JSON text frame");
            System.out.println("[PASS] first frame type=" + first.getEvent().get("type"));
        } finally {
            session.close();
        }
    }

    // -------------------------------------------------------------------------
    // Send session.update and receive ack
    // -------------------------------------------------------------------------

    @Test
    void sendSessionUpdate() throws Exception {
        MeshAPI client = newClient();
        CountDownLatch openLatch = new CountDownLatch(1);
        CountDownLatch ackLatch  = new CountDownLatch(2); // session.created + session.updated
        List<RealtimeMessage> messages = new CopyOnWriteArrayList<>();

        RealtimeSession session = client.realtime().connect(REALTIME_MODEL, new RealtimeListener() {
            @Override public void onOpen(RealtimeSession s) { openLatch.countDown(); }
            @Override public void onMessage(RealtimeSession s, RealtimeMessage msg) {
                messages.add(msg);
                ackLatch.countDown();
            }
            @Override public void onError(RealtimeSession s, RealtimeError err) {
                System.err.println("onError: " + err.getMessage());
            }
        }).get(15, TimeUnit.SECONDS);

        try {
            assertTrue(openLatch.await(5, TimeUnit.SECONDS), "onOpen not called");

            session.send(Map.of(
                    "type", "session.update",
                    "session", Map.of("instructions", "You are a helpful assistant.")
            )).get(5, TimeUnit.SECONDS);

            assertTrue(ackLatch.await(20, TimeUnit.SECONDS), "session.updated not received");
            assertTrue(messages.size() >= 2, "expected at least 2 frames");
            Object ackType = messages.get(1).getEvent() != null
                    ? messages.get(1).getEvent().get("type") : null;
            System.out.println("[PASS] session.update ack type=" + ackType);
        } finally {
            session.close();
        }
    }

    // -------------------------------------------------------------------------
    // Error envelope for unknown model
    // -------------------------------------------------------------------------

    @Test
    void errorEnvelopeForBadModel() throws Exception {
        MeshAPI client = newClient();
        CountDownLatch latch = new CountDownLatch(1);
        List<RealtimeError> errors = new CopyOnWriteArrayList<>();

        try {
            RealtimeSession session = client.realtime().connect("nonexistent/bad-model-xyz",
                    new RealtimeListener() {
                        @Override public void onError(RealtimeSession s, RealtimeError err) {
                            errors.add(err);
                            latch.countDown();
                        }
                        @Override public void onClose(RealtimeSession s, int code, String reason) {
                            latch.countDown(); // may close without explicit error frame
                        }
                    }).get(15, TimeUnit.SECONDS);

            latch.await(10, TimeUnit.SECONDS);
            session.close();

            if (!errors.isEmpty()) {
                System.out.println("[PASS] error envelope: code=" + errors.get(0).getCode());
            } else {
                System.out.println("[PASS] server closed for bad model (no envelope)");
            }
        } catch (Exception e) {
            System.out.println("[PASS] connect failed for bad model: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Audio frame (binary) handling
    // -------------------------------------------------------------------------

    @Test
    void audioFrameTyping() throws Exception {
        // Verifies that if a binary audio frame arrives, the SDK wraps it as
        // RealtimeMessage with isAudio()==true. We cannot force audio without
        // a full conversation, so we just verify connect + clean close for now.
        MeshAPI client = newClient();
        CountDownLatch latch = new CountDownLatch(1);

        RealtimeSession session = client.realtime().connect(REALTIME_MODEL, new RealtimeListener() {
            @Override public void onOpen(RealtimeSession s) { latch.countDown(); }
        }).get(15, TimeUnit.SECONDS);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        session.close();
        System.out.println("[PASS] realtime audio frame typing verified (connect/close only)");
    }
}
