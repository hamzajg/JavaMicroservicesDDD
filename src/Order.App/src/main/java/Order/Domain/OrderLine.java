package Order.App.src.main.java.Order.Domain;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLine {

    private final UUID productId;
    private final String sku;
    private final String name;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderLine(UUID productId, String sku, String name, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
