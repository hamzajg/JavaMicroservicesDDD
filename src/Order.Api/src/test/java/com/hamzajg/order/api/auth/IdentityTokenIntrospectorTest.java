package com.hamzajg.order.api.auth;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;

public class IdentityTokenIntrospectorTest {

    private HttpServer server;
    private IdentityTokenIntrospector introspector;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/identity/me", ex -> {
            String auth = ex.getRequestHeaders().getFirst("Authorization");
            int status = "Bearer good-token".equals(auth) ? 200 : 401;
            byte[] body = (status == 200 ? "{\"id\":\"u1\"}" : "{}").getBytes();
            ex.sendResponseHeaders(status, body.length);
            ex.getResponseBody().write(body);
            ex.close();
        });
        server.createContext("/stall", ex -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            ex.sendResponseHeaders(200, -1);
            ex.close();
        });
        server.start();
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        introspector = new IdentityTokenIntrospector(timeoutTemplate(), base);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    // covers scenario: Valid token reaches order logic
    @Test
    void validTokenIsValid() {
        assertEquals(IntrospectionResult.VALID, introspector.introspect("good-token"));
    }

    // covers scenario: Invalid token is rejected
    @Test
    void badTokenIsInvalid() {
        assertEquals(IntrospectionResult.INVALID, introspector.introspect("forged"));
    }

    // covers scenario: Identity outage fails closed
    @Test
    void unreachableIdentityIsUnavailable() {
        IdentityTokenIntrospector dead = new IdentityTokenIntrospector(
                timeoutTemplate(), "http://127.0.0.1:1");
        assertEquals(IntrospectionResult.UNAVAILABLE, dead.introspect("any"));
    }

    // covers scenario: Introspection call is bounded
    @Test
    void stalledIdentityIsUnavailableWithinTimeout() {
        IdentityTokenIntrospector stalled = new IdentityTokenIntrospector(
                timeoutTemplate(), "http://127.0.0.1:" + server.getAddress().getPort() + "/stall");
        long start = System.nanoTime();
        IntrospectionResult r = stalled.introspect("good-token");
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        assertEquals(IntrospectionResult.UNAVAILABLE, r);
        assertTrue(elapsedMs < 1500, "introspection should abort near timeout, took " + elapsedMs + "ms");
    }

    private static RestTemplate timeoutTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(500);
        factory.setReadTimeout(300);
        return new RestTemplate(factory);
    }
}
