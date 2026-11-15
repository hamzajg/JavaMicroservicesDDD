package Order.App.src.main.java.Order.App.UseCases;

import Services.Common.src.main.java.Services.Common.Commands.ICommand;
import java.util.List;
import java.util.UUID;

public class PlaceOrder implements ICommand {

    private final UUID customerId;
    private final List<OrderItem> items;

    public PlaceOrder(UUID customerId, List<OrderItem> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public static class OrderItem {
        private final UUID productId;
        private final int quantity;

        public OrderItem(UUID productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public UUID getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
