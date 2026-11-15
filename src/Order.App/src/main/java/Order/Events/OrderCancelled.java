package Order.App.src.main.java.Order.Events;

import Order.App.src.main.java.Order.Domain.*;
import Services.Common.src.main.java.Services.Common.Domain.Events.*;

public class OrderCancelled extends DomainEvent {

    private Order order;

    public OrderCancelled(Order order) {
        this.order = order;
    }

}
