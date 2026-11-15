package Order.App.src.main.java.Order.Events;

import Order.App.src.main.java.Order.Domain.*;
import Services.Common.src.main.java.Services.Common.Domain.Events.*;

public class OrderPlaced extends DomainEvent {

    private Order order;

    public OrderPlaced(Order order) {
        this.order = order;
    }

}
