package Order.App.src.main.java.Order.App.Repositories;

import Order.App.src.main.java.Order.Domain.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<UUID, Order> orders = new HashMap<>();

    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findByCustomerId(UUID customerId) {
        List<Order> matches = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerId() != null && order.getCustomerId().equals(customerId))
                matches.add(order);
        }
        return matches;
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
