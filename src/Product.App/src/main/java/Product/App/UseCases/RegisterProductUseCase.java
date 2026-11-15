package Product.App.src.main.java.Product.App.UseCases;

import Product.App.src.main.java.Product.App.Repositories.ProductRepository;
import Product.App.src.main.java.Product.Domain.Product;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;

public class RegisterProductUseCase implements IRegisterProductUseCase {

    private final IPublisherServices pubServices;
    private final ProductRepository productRepository;

    public RegisterProductUseCase(IPublisherServices pubServices, ProductRepository productRepository) {
        this.pubServices = pubServices;
        this.productRepository = productRepository;
    }

    @Override
    public Product register(RegisterProduct command) throws Exception {
        validate(command);
        if (productRepository.findBySku(command.getSku()).isPresent())
            throw new DuplicateSkuException("sku already registered: " + command.getSku());
        Product product = new Product(command.getSku(), command.getName(),
                command.getDescription(), command.getPrice());
        productRepository.save(product);
        product.getDomainEvents().forEach(pubServices::publish);
        return product;
    }

    @Override
    public void handle(RegisterProduct command) throws Exception {
        register(command);
    }

    private void validate(RegisterProduct command) {
        if (command == null)
            throw new IllegalArgumentException("command must not be null");
        if (isBlank(command.getSku()) || isBlank(command.getName()))
            throw new IllegalArgumentException("sku and name are required");
        if (command.getPrice() == null || command.getPrice().signum() <= 0)
            throw new IllegalArgumentException("price must be greater than zero");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
