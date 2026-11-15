package Order.App.src.main.java.Order.Domain;

import Order.App.src.main.java.Order.Events.*;
import Services.Common.src.main.java.Services.Common.Domain.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<UUID> {

    private final UUID customerId;
    private final List<OrderLine> lines;
    private OrderStatus status;

    protected Order(){
        this.customerId = null;
        this.lines = new ArrayList<>();
        this.status = OrderStatus.PLACED;
    }

    public Order(UUID customerId, List<OrderLine> lines) {
        super(UUID.randomUUID());
        this.customerId = customerId;
        this.lines = new ArrayList<>(lines);
        this.status = OrderStatus.PLACED;

        try {
            addDomainEvent(new OrderPlaced(this));
        }
        catch (Exception err) {
            System.out.println(err);
        }
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        try {
            addDomainEvent(new OrderCancelled(this));
        }
        catch (Exception err) {
            System.out.println(err);
        }
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return lines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
