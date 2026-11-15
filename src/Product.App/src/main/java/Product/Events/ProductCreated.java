package Product.App.src.main.java.Product.Events;

import Product.App.src.main.java.Product.Domain.*;
import Services.Common.src.main.java.Services.Common.Domain.Events.*;

public class ProductCreated extends DomainEvent {

    private Product product;

    public ProductCreated(Product product) {
        this.product = product;
    }

}
