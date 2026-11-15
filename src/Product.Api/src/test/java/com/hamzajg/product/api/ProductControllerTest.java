package com.hamzajg.product.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String payload(String sku) {
        return "{\"sku\":\"" + sku + "\",\"name\":\"Widget " + sku + "\","
                + "\"description\":\"A fine widget\",\"price\":19.99}";
    }

    @Test
    void registerReturns201WithIdAndRetrievable() throws Exception {
        // covers scenario: Register product via API
        MvcResult created = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON).content(payload("SKU-T1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.sku", is("SKU-T1")))
                .andExpect(jsonPath("$.price", is(19.99)))
                .andReturn();

        JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
        String id = body.get("id").asText();
        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Widget SKU-T1")))
                .andExpect(jsonPath("$.description", is("A fine widget")));
    }

    @Test
    void duplicateSkuReturns409AndInvalidReturns400() throws Exception {
        // covers scenario: Register invalid product rejected
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON).content(payload("SKU-T2")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON).content(payload("SKU-T2")))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"SKU-T3\",\"name\":\"\",\"description\":\"d\",\"price\":5.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listAndGetByIdBehave() throws Exception {
        // covers scenario: Query products
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON).content(payload("SKU-T4")))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        mockMvc.perform(get("/products/" + java.util.UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
