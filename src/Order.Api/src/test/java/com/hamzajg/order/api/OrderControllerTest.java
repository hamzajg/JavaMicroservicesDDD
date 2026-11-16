package com.hamzajg.order.api;

import com.hamzajg.order.api.auth.Introspection;
import com.hamzajg.order.api.auth.IntrospectionResult;
import com.hamzajg.order.api.auth.TokenIntrospector;
import Order.App.src.main.java.Order.App.Ports.CustomerDirectory;
import Order.App.src.main.java.Order.App.Ports.ProductCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerDirectory customerDirectory;

    @MockBean
    private ProductCatalog productCatalog;

    @MockBean
    private TokenIntrospector tokenIntrospector;

    private static final String AUTH = "Bearer test-token";

    @BeforeEach
    void validTokenByDefault() {
        when(tokenIntrospector.introspect(any()))
                .thenReturn(new Introspection(IntrospectionResult.VALID, "jane@example.com"));
    }

    private static final UUID CUSTOMER = UUID.randomUUID();
    private static final UUID PRODUCT_A = UUID.randomUUID();
    private static final UUID PRODUCT_B = UUID.randomUUID();

    private void stubHappyPath() {
        when(customerDirectory.customerExists(CUSTOMER)).thenReturn(true);
        when(productCatalog.findById(PRODUCT_A)).thenReturn(Optional.of(
                new ProductCatalog.ProductSnapshot(PRODUCT_A, "SKU-A", "Alpha", new BigDecimal("10.00"))));
        when(productCatalog.findById(PRODUCT_B)).thenReturn(Optional.of(
                new ProductCatalog.ProductSnapshot(PRODUCT_B, "SKU-B", "Beta", new BigDecimal("2.50"))));
    }

    private static String placePayload(UUID customerId, String items) {
        return "{\"customerId\":\"" + customerId + "\",\"items\":" + items + "}";
    }

    @Test
    void placeOrderReturns201PricedServerSide() throws Exception {
        // covers scenario: Place order via API
        stubHappyPath();
        String items = "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":2},"
                + "{\"productId\":\"" + PRODUCT_B + "\",\"quantity\":4}]";
        MvcResult created = mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER, items)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.status", is("PLACED")))
                .andExpect(jsonPath("$.total", is(30.00)))
                .andExpect(jsonPath("$.lines[0].sku", is("SKU-A")))
                .andExpect(jsonPath("$.lines[0].unitPrice", is(10.00)))
                .andReturn();

        JsonNode id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id");
        mockMvc.perform(get("/orders/" + id.asText()).header("Authorization", AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lines", hasSize(2)));
    }

    @Test
    void placeOrderRejectedCleanly() throws Exception {
        // covers scenario: Place order rejected cleanly
        when(customerDirectory.customerExists(any(UUID.class))).thenReturn(false);
        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(UUID.randomUUID(),
                                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":1}]")))
                .andExpect(status().isNotFound());

        when(customerDirectory.customerExists(CUSTOMER)).thenReturn(true);
        when(productCatalog.findById(any(UUID.class))).thenReturn(Optional.empty());
        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER,
                                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":1}]")))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER,
                                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":0}]")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void queryAndCancelOrder() throws Exception {
        // covers scenario: Query orders
        // covers scenario: Cancel order
        stubHappyPath();
        MvcResult first = mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER,
                                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":1}]")))
                .andExpect(status().isCreated()).andReturn();
        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER,
                                "[{\"productId\":\"" + PRODUCT_B + "\",\"quantity\":2}]")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/orders").header("Authorization", AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        mockMvc.perform(get("/orders/" + UUID.randomUUID()).header("Authorization", AUTH))
                .andExpect(status().isNotFound());

        String id = objectMapper.readTree(first.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/orders/" + id + "/cancel").header("Authorization", AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
        mockMvc.perform(post("/orders/" + id + "/cancel").header("Authorization", AUTH))
                .andExpect(status().isConflict());
    }

    @Test
    void upstreamFailureReturnsClean5xx() throws Exception {
        // covers scenario: Upstream server error returns a clean error
        // covers scenario: Upstream connection failure returns a clean error
        when(customerDirectory.customerExists(any(UUID.class)))
                .thenThrow(new UpstreamException("upstream unavailable", 502, null));
        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER,
                                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":1}]")))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(not(containsString("Exception"))));

        when(customerDirectory.customerExists(any(UUID.class)))
                .thenThrow(new UpstreamException("upstream timed out", 504, null));
        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placePayload(CUSTOMER,
                                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":1}]")))
                .andExpect(status().isGatewayTimeout());
    }

    @Test
    void authEnforcement() throws Exception {
        String body = placePayload(CUSTOMER,
                "[{\"productId\":\"" + PRODUCT_A + "\",\"quantity\":1}]");

        // covers scenario: Missing token is rejected
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());

        // covers scenario: Invalid token is rejected
        when(tokenIntrospector.introspect("bad-token")).thenReturn(Introspection.of(IntrospectionResult.INVALID));
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer bad-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        // covers scenario: Identity outage fails closed
        when(tokenIntrospector.introspect("any-token")).thenReturn(Introspection.of(IntrospectionResult.UNAVAILABLE));
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer any-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isServiceUnavailable());

        // covers scenario: Valid token reaches order logic
        stubHappyPath();
        mockMvc.perform(post("/orders")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}
