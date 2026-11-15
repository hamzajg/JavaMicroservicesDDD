package com.hamzajg.product.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class ProductRequest {

    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal price;

    @JsonCreator
    public ProductRequest(@JsonProperty("sku") String sku,
                          @JsonProperty("name") String name,
                          @JsonProperty("description") String description,
                          @JsonProperty("price") BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
}
