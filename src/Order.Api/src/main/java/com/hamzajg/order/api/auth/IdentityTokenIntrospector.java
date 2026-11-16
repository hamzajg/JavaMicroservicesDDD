package com.hamzajg.order.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class IdentityTokenIntrospector implements TokenIntrospector {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String identityBaseUrl;

    public IdentityTokenIntrospector(RestTemplate restTemplate,
                                     @Value("${identity.api.url:http://localhost:8082}") String identityBaseUrl) {
        this.restTemplate = restTemplate;
        this.identityBaseUrl = identityBaseUrl;
    }

    @Override
    public Introspection introspect(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        try {
            String body = restTemplate.exchange(identityBaseUrl + "/identity/me",
                    HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
            return new Introspection(IntrospectionResult.VALID, emailOf(body));
        }
        catch (HttpClientErrorException.Unauthorized e) {
            return Introspection.of(IntrospectionResult.INVALID);
        }
        catch (RuntimeException e) {
            return Introspection.of(IntrospectionResult.UNAVAILABLE);
        }
    }

    /** Reads the caller's email out of Identity's UserResponse; null if absent/unreadable. */
    private String emailOf(String body) {
        try {
            JsonNode email = objectMapper.readTree(body).get("email");
            return email == null ? null : email.asText();
        }
        catch (Exception e) {
            return null;
        }
    }
}
