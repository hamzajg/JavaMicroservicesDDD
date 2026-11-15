package Product.App.src.main.java.Product.App.UseCases;

import Product.App.src.main.java.Product.App.Repositories.ProductRepository;
import Product.App.src.main.java.Product.Domain.Product;
import java.util.List;

public class ListProductsUseCase {

    private final ProductRepository productRepository;

    public ListProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> list() {
        return productRepository.findAll();
    }
}
