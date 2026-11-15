package com.hamzajg.product.api;

import Product.App.src.main.java.Product.App.UseCases.DuplicateSkuException;
import Product.App.src.main.java.Product.App.UseCases.GetProductUseCase;
import Product.App.src.main.java.Product.App.UseCases.IRegisterProductUseCase;
import Product.App.src.main.java.Product.App.UseCases.ListProductsUseCase;
import Product.App.src.main.java.Product.App.UseCases.RegisterProduct;
import Product.App.src.main.java.Product.Domain.Product;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final IRegisterProductUseCase registerProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;

    public ProductController(IRegisterProductUseCase registerProductUseCase,
                             GetProductUseCase getProductUseCase,
                             ListProductsUseCase listProductsUseCase) {
        this.registerProductUseCase = registerProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> register(@RequestBody ProductRequest request) {
        try {
            Product product = registerProductUseCase.register(new RegisterProduct(
                    request.getSku(), request.getName(), request.getDescription(), request.getPrice()));
            return ResponseEntity.created(URI.create("/products/" + product.getId()))
                    .body(new ProductResponse(product));
        } catch (DuplicateSkuException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public List<ProductResponse> list() {
        return listProductsUseCase.list().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable UUID id) {
        return getProductUseCase.get(id)
                .map(ProductResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }
}
