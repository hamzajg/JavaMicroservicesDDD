package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.App.Repositories.OrderRepository;
import Order.App.src.main.java.Order.Domain.Order;
import java.util.Optional;
import java.util.UUID;

public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> get(UUID id) {
        return orderRepository.findById(id);
    }
}
