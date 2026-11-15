package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.App.Ports.CustomerDirectory;
import Order.App.src.main.java.Order.App.Ports.ProductCatalog;
import Order.App.src.main.java.Order.App.Repositories.OrderRepository;
import Order.App.src.main.java.Order.Domain.Order;
import Order.App.src.main.java.Order.Domain.OrderLine;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderUseCase implements IPlaceOrderUseCase {

    private final IPublisherServices pubServices;
    private final OrderRepository orderRepository;
    private final CustomerDirectory customerDirectory;
    private final ProductCatalog productCatalog;

    public PlaceOrderUseCase(IPublisherServices pubServices, OrderRepository orderRepository,
                             CustomerDirectory customerDirectory, ProductCatalog productCatalog) {
        this.pubServices = pubServices;
        this.orderRepository = orderRepository;
        this.customerDirectory = customerDirectory;
        this.productCatalog = productCatalog;
    }

    @Override
    public Order place(PlaceOrder command) throws Exception {
        validate(command);
        if (!customerDirectory.customerExists(command.getCustomerId()))
            throw new CustomerNotFoundException("customer not found: " + command.getCustomerId());

        List<OrderLine> lines = new ArrayList<>();
        for (PlaceOrder.OrderItem item : command.getItems()) {
            ProductCatalog.ProductSnapshot product = productCatalog.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("product not found: " + item.getProductId()));
            lines.add(new OrderLine(product.getId(), product.getSku(), product.getName(),
                    item.getQuantity(), product.getPrice()));
        }

        Order order = new Order(command.getCustomerId(), lines);
        orderRepository.save(order);
        order.getDomainEvents().forEach(pubServices::publish);
        return order;
    }

    @Override
    public void handle(PlaceOrder command) throws Exception {
        place(command);
    }

    private void validate(PlaceOrder command) {
        if (command == null)
            throw new IllegalArgumentException("command must not be null");
        if (command.getCustomerId() == null)
            throw new IllegalArgumentException("customerId is required");
        if (command.getItems() == null || command.getItems().isEmpty())
            throw new IllegalArgumentException("at least one item is required");
        for (PlaceOrder.OrderItem item : command.getItems()) {
            if (item.getProductId() == null || item.getQuantity() < 1)
                throw new IllegalArgumentException("each item needs a productId and quantity >= 1");
        }
    }
}
