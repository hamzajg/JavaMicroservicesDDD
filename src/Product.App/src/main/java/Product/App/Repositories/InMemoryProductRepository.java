package Product.App.src.main.java.Product.App.Repositories;

import Product.App.src.main.java.Product.Domain.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    public void save(Product product) {
        products.put(product.getId(), product);
    }

    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    public Optional<Product> findBySku(String sku) {
        return products.values().stream()
                .filter(p -> p.getSku().equals(sku))
                .findFirst();
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
