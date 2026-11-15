package com.hamzajg.product.api;

import Product.App.src.main.java.Product.App.Repositories.ProductRepository;
import Product.App.src.main.java.Product.App.Repositories.InMemoryProductRepository;
import Product.App.src.main.java.Product.App.UseCases.GetProductUseCase;
import Product.App.src.main.java.Product.App.UseCases.IRegisterProductUseCase;
import Product.App.src.main.java.Product.App.UseCases.ListProductsUseCase;
import Product.App.src.main.java.Product.App.UseCases.RegisterProductUseCase;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import Services.Common.src.main.java.Services.Common.Services.InMemoryPublisherServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    @Bean
    public IPublisherServices publisherServices() {
        return new InMemoryPublisherServices();
    }

    @Bean
    public ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }

    @Bean
    public IRegisterProductUseCase registerProductUseCase(IPublisherServices publisherServices,
                                                         ProductRepository productRepository) {
        return new RegisterProductUseCase(publisherServices, productRepository);
    }

    @Bean
    public GetProductUseCase getProductUseCase(ProductRepository productRepository) {
        return new GetProductUseCase(productRepository);
    }

    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepository productRepository) {
        return new ListProductsUseCase(productRepository);
    }
}
