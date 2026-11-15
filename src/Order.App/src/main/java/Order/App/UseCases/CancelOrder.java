package Order.App.src.main.java.Order.App.UseCases;

import Services.Common.src.main.java.Services.Common.Commands.ICommand;
import java.util.UUID;

public class CancelOrder implements ICommand {

    private final UUID orderId;

    public CancelOrder(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
