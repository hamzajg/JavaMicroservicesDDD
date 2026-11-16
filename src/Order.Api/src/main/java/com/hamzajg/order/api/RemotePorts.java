package com.hamzajg.order.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import Order.App.src.main.java.Order.App.Ports.CustomerDirectory;
import Order.App.src.main.java.Order.App.Ports.ProductCatalog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class RemotePorts {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String customerBaseUrl;
    private final String productBaseUrl;

    public RemotePorts(RestTemplate restTemplate,
                       @Value("${customer.api.url:http://localhost:8080}") String customerBaseUrl,
                       @Value("${product.api.url:http://localhost:8081}") String productBaseUrl) {
        this.restTemplate = restTemplate;
        this.customerBaseUrl = customerBaseUrl;
        this.productBaseUrl = productBaseUrl;
    }

    public CustomerDirectory customerDirectory() {
        return new CustomerDirectory() {
            @Override
            public boolean customerExists(UUID customerId) {
                try {
                    restTemplate.exchange(customerBaseUrl + "/customers/" + customerId,
                            HttpMethod.GET, null, String.class);
                    return true;
                }
                catch (HttpClientErrorException.NotFound e) {
                    return false;
                }
                catch (RestClientException e) {
                    throw UpstreamException.from(e);
                }
            }

            @Override
            public Optional<UUID> findByEmail(String email) {
                try {
                    String body = restTemplate.getForObject(
                            customerBaseUrl + "/customers/by-email?email={email}",
                            String.class, email == null ? "" : email);
                    JsonNode node = objectMapper.readTree(body);
                    return Optional.of(UUID.fromString(node.get("id").asText()));
                }
                catch (HttpClientErrorException.NotFound e) {
                    return Optional.empty();
                }
                catch (RestClientException e) {
                    throw UpstreamException.from(e);
                }
                catch (Exception e) {
                    throw UpstreamException.badGateway("customer directory returned an unreadable response", e);
                }
            }
        };
    }

    public ProductCatalog productCatalog() {
        return productId -> {
            try {
                String body = restTemplate.getForObject(productBaseUrl + "/products/" + productId, String.class);
                JsonNode node = objectMapper.readTree(body);
                return Optional.of(new ProductCatalog.ProductSnapshot(
                        UUID.fromString(node.get("id").asText()),
                        node.get("sku").asText(),
                        node.get("name").asText(),
                        new BigDecimal(node.get("price").asText())));
            }
            catch (HttpClientErrorException.NotFound e) {
                return Optional.empty();
            }
            catch (RestClientException e) {
                throw UpstreamException.from(e);
            }
            catch (Exception e) {
                throw UpstreamException.badGateway("product catalog returned an unreadable response", e);
            }
        };
    }
}
