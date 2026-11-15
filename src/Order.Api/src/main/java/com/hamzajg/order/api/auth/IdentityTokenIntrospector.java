package com.hamzajg.order.api.auth;

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
    private final String identityBaseUrl;

    public IdentityTokenIntrospector(RestTemplate restTemplate,
                                     @Value("${identity.api.url:http://localhost:8082}") String identityBaseUrl) {
        this.restTemplate = restTemplate;
        this.identityBaseUrl = identityBaseUrl;
    }

    @Override
    public IntrospectionResult introspect(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        try {
            restTemplate.exchange(identityBaseUrl + "/identity/me",
                    HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return IntrospectionResult.VALID;
        }
        catch (HttpClientErrorException.Unauthorized e) {
            return IntrospectionResult.INVALID;
        }
        catch (RuntimeException e) {
            return IntrospectionResult.UNAVAILABLE;
        }
    }
}
