package Product.App.src.main.java.Product.App.Repositories;

import Product.App.src.main.java.Product.Domain.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(UUID id);

    Optional<Product> findBySku(String sku);

    List<Product> findAll();
}
