package com.hamzajg.product.api;

import Product.App.src.main.java.Product.Domain.Product;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductResponse {

    private final UUID id;
    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal price;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.sku = product.getSku();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
}
