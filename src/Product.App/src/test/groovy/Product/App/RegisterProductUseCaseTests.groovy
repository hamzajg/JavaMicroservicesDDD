package Product.App;

import spock.lang.*
import Product.App.src.main.java.Product.App.UseCases.*
import Product.App.src.main.java.Product.App.Repositories.*
import Product.App.src.main.java.Product.Domain.*
import Product.App.src.main.java.Product.Events.*
import Services.Common.src.main.java.Services.Common.Services.*
import java.math.BigDecimal

public class RegisterProductUseCaseTests extends Specification {

    IPublisherServices pubServices
    ProductRepository productRepository
    RegisterProductUseCase sutRPUseCase

    def setup() {
        pubServices = new InMemoryPublisherServices()
        productRepository = new InMemoryProductRepository()
        sutRPUseCase = new RegisterProductUseCase(pubServices, productRepository)
    }

    def "Should be able to register a product"() {
        when:
        sutRPUseCase.handle(new RegisterProduct("SKU-1", "Widget", "A widget", new BigDecimal("9.99")))
        then:
        ((InMemoryPublisherServices) pubServices).getEvents().size() > 0
    }

    def "Should return the registered product with a server id"() {
        when:
        def product = sutRPUseCase.register(new RegisterProduct("SKU-1", "Widget", "A widget", new BigDecimal("9.99")))
        then:
        product.id != null
        productRepository.findById(product.id).isPresent()
        productRepository.findBySku("SKU-1").isPresent()
    }

    def "Should publish a ProductCreated domain event on registration"() {
        when:
        sutRPUseCase.register(new RegisterProduct("SKU-1", "Widget", "A widget", new BigDecimal("9.99")))
        then:
        // covers scenario: Registration publishes event
        def events = ((InMemoryPublisherServices) pubServices).getEvents()
        events.size() == 1
        events[0] instanceof ProductCreated
    }

    def "Should reject duplicate sku"() {
        given:
        sutRPUseCase.register(new RegisterProduct("SKU-1", "Widget", "A widget", new BigDecimal("9.99")))

        when:
        sutRPUseCase.register(new RegisterProduct("SKU-1", "Other", "Other desc", new BigDecimal("1.00")))
        then:
        thrown(DuplicateSkuException)
        productRepository.findAll().size() == 1
    }

    def "Should reject invalid product data"() {
        when:
        sutRPUseCase.register(new RegisterProduct("SKU-1", " ", "desc", new BigDecimal("9.99")))
        then:
        thrown(IllegalArgumentException)

        when:
        sutRPUseCase.register(new RegisterProduct("SKU-2", "Name", "desc", BigDecimal.ZERO))
        then:
        thrown(IllegalArgumentException)
    }

    def "Should list products and fetch by id"() {
        given:
        def first = sutRPUseCase.register(new RegisterProduct("SKU-1", "One", "d", new BigDecimal("1.00")))
        def second = sutRPUseCase.register(new RegisterProduct("SKU-2", "Two", "d", new BigDecimal("2.00")))
        def listUseCase = new ListProductsUseCase(productRepository)
        def getUseCase = new GetProductUseCase(productRepository)

        expect:
        listUseCase.list().size() == 2
        getUseCase.get(first.id).get().name == "One"
        !getUseCase.get(UUID.randomUUID()).isPresent()
    }
}
