package com.hamzajg.order.api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;

public class RemotePortsTest {

    private static final String CUSTOMER_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

    private HttpServer server;
    private String baseUrl;
    private RemotePorts ports;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/stall", ex -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            ex.sendResponseHeaders(200, -1);
            ex.close();
        });
        server.createContext("/fail", ex -> respond(ex, 500, "{}"));
        server.createContext("/missing", ex -> respond(ex, 404, "{}"));
        server.createContext("/customers/by-email", ex -> respond(ex, 200,
                "{\"id\":\"" + CUSTOMER_ID + "\",\"email\":\"ada@example.com\"}"));
        server.createContext("/ok", ex -> respond(ex, 200, "{\"id\":\"" + UUID.randomUUID()
                + "\",\"sku\":\"S1\",\"name\":\"Thing\",\"price\":\"9.99\"}"));
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();

        ports = new RemotePorts(timeoutTemplate(), baseUrl + "/ok", baseUrl + "/ok");
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange ex, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    // covers scenario: Upstream timeout returns a clean error
    @Test
    void readTimeoutBecomes504() {
        RemotePorts stalled = new RemotePorts(
                timeoutTemplate(), "http://127.0.0.1:" + server.getAddress().getPort() + "/stall",
                "http://127.0.0.1:" + server.getAddress().getPort() + "/stall");
        UpstreamException e = assertThrows(UpstreamException.class,
                () -> stalled.customerDirectory().customerExists(UUID.randomUUID()));
        assertEquals(504, e.getStatus());
    }

    // covers scenario: Upstream connection failure returns a clean error
    @Test
    void connectionRefusedBecomes502() {
        RemotePorts dead = new RemotePorts(timeoutTemplate(),
                "http://127.0.0.1:1", "http://127.0.0.1:1");
        UpstreamException e = assertThrows(UpstreamException.class,
                () -> dead.customerDirectory().customerExists(UUID.randomUUID()));
        assertEquals(502, e.getStatus());
    }

    // covers scenario: Upstream server error returns a clean error
    @Test
    void upstream500Becomes502() {
        RemotePorts failing = new RemotePorts(timeoutTemplate(),
                baseUrl + "/fail", baseUrl + "/ok");
        UpstreamException e = assertThrows(UpstreamException.class,
                () -> failing.customerDirectory().customerExists(UUID.randomUUID()));
        assertEquals(502, e.getStatus());
    }

    // covers scenario: Unknown references still return 404
    @Test
    void notFoundStillMapsToEmpty() {
        RemotePorts missing = new RemotePorts(timeoutTemplate(),
                baseUrl + "/missing", baseUrl + "/missing");
        assertFalse(missing.customerDirectory().customerExists(UUID.randomUUID()));
        assertEquals(Optional.empty(), missing.productCatalog().findById(UUID.randomUUID()));
    }

    @Test
    void happyPathStillParses() {
        assertTrue(ports.customerDirectory().customerExists(UUID.randomUUID()));
        Optional<?> snap = ports.productCatalog().findById(UUID.randomUUID());
        assertTrue(snap.isPresent());
    }

    // covers scenario: A caller is resolved to their customer by email
    @Test
    void findByEmailResolvesTheCustomerId() {
        RemotePorts lookup = new RemotePorts(timeoutTemplate(), baseUrl, baseUrl + "/ok");
        assertEquals(Optional.of(UUID.fromString(CUSTOMER_ID)),
                lookup.customerDirectory().findByEmail("ada@example.com"));
    }

    // covers scenario: An unknown caller email resolves to no customer
    @Test
    void findByEmailUnknownEmailIsEmpty() {
        RemotePorts missing = new RemotePorts(timeoutTemplate(), baseUrl + "/missing", baseUrl + "/ok");
        assertEquals(Optional.empty(), missing.customerDirectory().findByEmail("ghost@example.com"));
    }

    private static RestTemplate timeoutTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(500);
        factory.setReadTimeout(300);
        return new RestTemplate(factory);
    }

    // covers scenario: Upstream calls are bounded by configured timeouts
    @Test
    void restTemplateBeanTimeoutsActuallyBoundCalls() {
        RestTemplate rt = new OrderConfig().restTemplate(100, 100);
        RemotePorts stalled = new RemotePorts(rt,
                baseUrl + "/stall", baseUrl + "/stall");
        long start = System.nanoTime();
        UpstreamException e = assertThrows(UpstreamException.class,
                () -> stalled.customerDirectory().customerExists(UUID.randomUUID()));
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        assertEquals(504, e.getStatus());
        assertTrue(elapsedMs < 1500, "call should abort near the 100ms timeout, took " + elapsedMs + "ms");
    }
}
