package com.hamzajg.identity.api;

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
public class IdentityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String registerPayload(String email) {
        return "{\"email\":\"" + email + "\",\"displayName\":\"Jane Doe\",\"password\":\"password123\"}";
    }

    private static String loginPayload(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    @Test
    void registerReturns201AndRejectsDuplicateAndInvalid() throws Exception {
        // covers scenario: Register user via API
        mockMvc.perform(post("/identity/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registerPayload("jane@example.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.email", is("jane@example.com")));
        mockMvc.perform(post("/identity/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registerPayload("jane@example.com")))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/identity/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad\",\"displayName\":\"X\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginIssuesTokenAndRejectsBadPassword() throws Exception {
        // covers scenario: Login issues token
        mockMvc.perform(post("/identity/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registerPayload("john@example.com")))
                .andExpect(status().isCreated());
        MvcResult login = mockMvc.perform(post("/identity/login")
                        .contentType(MediaType.APPLICATION_JSON).content(loginPayload("john@example.com", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andReturn();
        mockMvc.perform(post("/identity/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("john@example.com", "wrong-password")))
                .andExpect(status().isUnauthorized());

        JsonNode token = objectMapper.readTree(login.getResponse().getContentAsString()).get("token");
        mockMvc.perform(get("/identity/me")
                        .header("Authorization", "Bearer " + token.asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Jane Doe")));
    }

    @Test
    void meRequiresValidBearerToken() throws Exception {
        // covers scenario: Authenticated me endpoint
        mockMvc.perform(get("/identity/me"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/identity/me")
                        .header("Authorization", "Bearer bogus-token"))
                .andExpect(status().isUnauthorized());
    }
}
