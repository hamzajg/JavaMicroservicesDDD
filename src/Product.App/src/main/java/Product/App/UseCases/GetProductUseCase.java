package Product.App.src.main.java.Product.App.UseCases;

import Product.App.src.main.java.Product.App.Repositories.ProductRepository;
import Product.App.src.main.java.Product.Domain.Product;
import java.util.Optional;
import java.util.UUID;

public class GetProductUseCase {

    private final ProductRepository productRepository;

    public GetProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> get(UUID id) {
        return productRepository.findById(id);
    }
}
