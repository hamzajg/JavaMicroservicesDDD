package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.App.Repositories.OrderRepository;
import Order.App.src.main.java.Order.Domain.Order;
import Order.App.src.main.java.Order.Domain.OrderStatus;
import Order.App.src.main.java.Order.Events.OrderCancelled;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import java.util.UUID;

public class CancelOrderUseCase implements ICancelOrderUseCase {

    private final IPublisherServices pubServices;
    private final OrderRepository orderRepository;

    public CancelOrderUseCase(IPublisherServices pubServices, OrderRepository orderRepository) {
        this.pubServices = pubServices;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order cancel(CancelOrder command) throws Exception {
        if (command == null || command.getOrderId() == null)
            throw new IllegalArgumentException("orderId is required");
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("order not found: " + command.getOrderId()));
        if (order.getStatus() != OrderStatus.PLACED)
            throw new InvalidOrderStateException("order is not cancellable: " + order.getStatus());
        order.cancel();
        orderRepository.save(order);
        order.getDomainEvents().stream()
                .filter(e -> e instanceof OrderCancelled)
                .forEach(pubServices::publish);
        return order;
    }

    @Override
    public void handle(CancelOrder command) throws Exception {
        cancel(command);
    }
}
