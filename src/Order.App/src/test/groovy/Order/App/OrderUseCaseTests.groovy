package Order.App;

import spock.lang.*
import Order.App.src.main.java.Order.App.UseCases.*
import Order.App.src.main.java.Order.App.Repositories.*
import Order.App.src.main.java.Order.App.Ports.*
import Order.App.src.main.java.Order.Domain.*
import Order.App.src.main.java.Order.Events.*
import Services.Common.src.main.java.Services.Common.Services.*
import java.math.BigDecimal
import java.util.*

public class OrderUseCaseTests extends Specification {

    IPublisherServices pubServices
    OrderRepository orderRepository
    CustomerDirectory customerDirectory
    ProductCatalog productCatalog
    PlaceOrderUseCase placeUseCase
    CancelOrderUseCase cancelUseCase
    GetOrderUseCase getUseCase
    ListOrdersUseCase listUseCase
    ResolveCustomerUseCase resolveCustomerUseCase

    UUID customerId = UUID.randomUUID()
    UUID productA = UUID.randomUUID()
    UUID productB = UUID.randomUUID()

    def setup() {
        pubServices = new InMemoryPublisherServices()
        orderRepository = new InMemoryOrderRepository()
        customerDirectory = Stub(CustomerDirectory) {
            customerExists(_) >> { args -> args[0] == customerId }
            findByEmail(_) >> { args -> args[0] == "jane@example.com" ? Optional.of(customerId) : Optional.empty() }
        }
        productCatalog = Stub(ProductCatalog) {
            findById(_) >> { args ->
                if (args[0] == productA) Optional.of(new ProductCatalog.ProductSnapshot(productA, "SKU-A", "Alpha", new BigDecimal("10.00")))
                else if (args[0] == productB) Optional.of(new ProductCatalog.ProductSnapshot(productB, "SKU-B", "Beta", new BigDecimal("2.50")))
                else Optional.empty()
            }
        }
        placeUseCase = new PlaceOrderUseCase(pubServices, orderRepository, customerDirectory, productCatalog)
        cancelUseCase = new CancelOrderUseCase(pubServices, orderRepository)
        getUseCase = new GetOrderUseCase(orderRepository)
        listUseCase = new ListOrdersUseCase(orderRepository)
        resolveCustomerUseCase = new ResolveCustomerUseCase(customerDirectory)
    }

    def "Should place an order priced from the catalog and publish OrderPlaced"() {
        when:
        def order = placeUseCase.place(new PlaceOrder(customerId, [
                new PlaceOrder.OrderItem(productA, 2),
                new PlaceOrder.OrderItem(productB, 4)
        ]))
        then:
        // covers scenario: Order events published
        order.id != null
        order.status == OrderStatus.PLACED
        order.lines.size() == 2
        order.lines[0].unitPrice == new BigDecimal("10.00")
        order.lines[0].sku == "SKU-A"
        order.total == new BigDecimal("30.00")
        def events = ((InMemoryPublisherServices) pubServices).getEvents()
        events.size() == 1
        events[0] instanceof OrderPlaced
    }

    def "Should reject unknown customer and unknown product"() {
        when:
        placeUseCase.place(new PlaceOrder(UUID.randomUUID(), [new PlaceOrder.OrderItem(productA, 1)]))
        then:
        thrown(CustomerNotFoundException)

        when:
        placeUseCase.place(new PlaceOrder(customerId, [new PlaceOrder.OrderItem(UUID.randomUUID(), 1)]))
        then:
        thrown(ProductNotFoundException)
        orderRepository.findAll().isEmpty()
    }

    def "Should reject invalid quantities"() {
        when:
        placeUseCase.place(new PlaceOrder(customerId, [new PlaceOrder.OrderItem(productA, 0)]))
        then:
        thrown(IllegalArgumentException)

        when:
        placeUseCase.place(new PlaceOrder(customerId, []))
        then:
        thrown(IllegalArgumentException)
    }

    def "Should cancel a placed order once and publish OrderCancelled"() {
        given:
        def order = placeUseCase.place(new PlaceOrder(customerId, [new PlaceOrder.OrderItem(productA, 1)]))

        when:
        def cancelled = cancelUseCase.cancel(new CancelOrder(order.id))
        then:
        // covers scenario: Order events published
        cancelled.status == OrderStatus.CANCELLED
        ((InMemoryPublisherServices) pubServices).getEvents().any { it instanceof OrderCancelled }

        when:
        cancelUseCase.cancel(new CancelOrder(order.id))
        then:
        thrown(InvalidOrderStateException)
    }

    def "Should reject cancel of unknown order"() {
        when:
        cancelUseCase.cancel(new CancelOrder(UUID.randomUUID()))
        then:
        thrown(OrderNotFoundException)
    }

    def "Should list orders and fetch by id"() {
        given:
        def first = placeUseCase.place(new PlaceOrder(customerId, [new PlaceOrder.OrderItem(productA, 1)]))
        def second = placeUseCase.place(new PlaceOrder(customerId, [new PlaceOrder.OrderItem(productB, 2)]))

        expect:
        listUseCase.list().size() == 2
        getUseCase.get(first.id).get().customerId == customerId
        !getUseCase.get(UUID.randomUUID()).isPresent()
    }
}
