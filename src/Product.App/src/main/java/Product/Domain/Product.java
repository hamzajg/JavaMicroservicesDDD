package Product.App.src.main.java.Product.Domain;

import Product.App.src.main.java.Product.Events.*;
import Services.Common.src.main.java.Services.Common.Domain.*;
import java.math.BigDecimal;
import java.util.UUID;

public class Product extends AggregateRoot<UUID> {

    private String sku;
    private String name;
    private String description;
    private BigDecimal price;

    protected Product(){

    }

    public Product(String sku, String name, String description, BigDecimal price) {
        super(UUID.randomUUID());
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;

        try {
            addDomainEvent(new ProductCreated(this));
        }
        catch (Exception err) {
            System.out.println(err);
        }
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

}
