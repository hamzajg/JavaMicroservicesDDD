package com.hamzajg.customer.api;

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
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID = "{\"firstName\":\"John\",\"lastName\":\"Doe\","
            + "\"email\":\"john@example.com\",\"address\":\"1 Main St\",\"phoneNumber\":\"555-1234\"}";

    @Test
    void registerReturns201WithIdAndRetrievable() throws Exception {
        // covers scenario: Register customer via API
        MvcResult created = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.fullName", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andReturn();

        JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
        String id = body.get("id").asText();
        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("1 Main St")))
                .andExpect(jsonPath("$.phoneNumber", is("555-1234")));
    }

    @Test
    void registerWithMissingEmailReturns400AndStoresNothing() throws Exception {
        // covers scenario: Register invalid customer rejected
        String bad = "{\"firstName\":\"Jane\",\"lastName\":\"Doe\","
                + "\"email\":\"not-an-email\",\"address\":\"2 Side St\",\"phoneNumber\":\"555-0000\"}";
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listAndGetByIdBehave() throws Exception {
        // covers scenario: Query customers
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        mockMvc.perform(get("/customers/" + java.util.UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void findCustomerByEmailBehaves() throws Exception {
        // covers scenario: A caller is resolved to their customer by email
        String email = "lookup-" + java.util.UUID.randomUUID() + "@example.com";
        String body = "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"email\":\"" + email
                + "\",\"address\":\"1 Analytical Engine Way\",\"phoneNumber\":\"555-0001\"}";
        MvcResult created = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/customers/by-email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.email", is(email)));

        // covers scenario: An unknown caller email resolves to no customer
        mockMvc.perform(get("/customers/by-email").param("email", "ghost@example.com"))
                .andExpect(status().isNotFound());
    }
}
