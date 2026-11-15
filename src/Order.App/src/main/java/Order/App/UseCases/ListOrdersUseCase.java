package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.App.Repositories.OrderRepository;
import Order.App.src.main.java.Order.Domain.Order;
import java.util.List;

public class ListOrdersUseCase {

    private final OrderRepository orderRepository;

    public ListOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }
}
