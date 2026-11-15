package com.hamzajg.order.api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
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
import Order.App.src.main.java.Order.App.Ports.ProductCatalog;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract test: pins the Customer/Product response shapes that
 * {@link RemotePorts} parses. The fixtures mirror the DTOs serialized by
 * Customer.Api ({@code CustomerResponse}) and Product.Api
 * ({@code ProductResponse}) — if either API renames or removes a field
 * RemotePorts reads, these tests fail loudly instead of breaking ordering
 * at runtime.
 */
public class UpstreamContractTest {

    private HttpServer server;
    private String baseUrl;
    private RemotePorts ports;
    private volatile String brokenPayload;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            byte[] body;
            if (brokenPayload != null) {
                body = brokenPayload.getBytes(StandardCharsets.UTF_8);
            } else {
                String fixture = path.startsWith("/products/")
                        ? "/contracts/product-response.json"
                        : "/contracts/customer-response.json";
                try (InputStream in = getClass().getResourceAsStream(fixture)) {
                    body = in.readAllBytes();
                }
            }
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(200, body.length);
            ex.getResponseBody().write(body);
            ex.close();
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(1000);
        ports = new RemotePorts(new RestTemplate(factory), baseUrl, baseUrl);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    // covers scenario: Upstream response contract is pinned
    @Test
    void productResponseFieldsAreParsed() {
        Optional<ProductCatalog.ProductSnapshot> snap =
                ports.productCatalog().findById(UUID.randomUUID());
        assertTrue(snap.isPresent());
        ProductCatalog.ProductSnapshot s = snap.get();
        assertEquals(UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7"), s.getId());
        assertEquals("SKU-100", s.getSku());
        assertEquals("Mechanical Keyboard", s.getName());
        assertEquals(new BigDecimal("129.99"), s.getPrice());
    }

    // covers scenario: Upstream response contract is pinned
    @Test
    void customerResponseIsAccepted() {
        assertTrue(ports.customerDirectory().customerExists(UUID.randomUUID()));
    }

    // covers scenario: Upstream response contract is pinned
    @Test
    void missingPriceFieldFailsLoudly() {
        brokenPayload = "{\"id\":\"7c9e6679-7425-40de-944b-e07fc1f90ae7\","
                + "\"sku\":\"SKU-100\",\"name\":\"Keyboard\",\"description\":\"x\"}";
        UpstreamException e = assertThrows(UpstreamException.class,
                () -> ports.productCatalog().findById(UUID.randomUUID()));
        assertEquals(502, e.getStatus());
    }
}
