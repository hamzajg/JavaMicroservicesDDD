package com.hamzajg.order.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public class PlaceOrderRequest {

    private final List<ItemRequest> items;

    @JsonCreator
    public PlaceOrderRequest(@JsonProperty("items") List<ItemRequest> items) {
        this.items = items;
    }

    public List<ItemRequest> getItems() { return items; }

    public static class ItemRequest {
        private final UUID productId;
        private final int quantity;

        @JsonCreator
        public ItemRequest(@JsonProperty("productId") UUID productId,
                           @JsonProperty("quantity") int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public UUID getProductId() { return productId; }
        public int getQuantity() { return quantity; }
    }
}
