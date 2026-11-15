package Order.App.src.main.java.Order.App.Ports;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductCatalog {

    Optional<ProductSnapshot> findById(UUID productId);

    class ProductSnapshot {
        private final UUID id;
        private final String sku;
        private final String name;
        private final BigDecimal price;

        public ProductSnapshot(UUID id, String sku, String name, BigDecimal price) {
            this.id = id;
            this.sku = sku;
            this.name = name;
            this.price = price;
        }

        public UUID getId() { return id; }
        public String getSku() { return sku; }
        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
    }
}
