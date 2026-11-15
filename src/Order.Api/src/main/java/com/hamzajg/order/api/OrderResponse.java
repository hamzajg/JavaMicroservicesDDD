package com.hamzajg.order.api;

import Order.App.src.main.java.Order.Domain.Order;
import Order.App.src.main.java.Order.Domain.OrderLine;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderResponse {

    private final UUID id;
    private final UUID customerId;
    private final String status;
    private final BigDecimal total;
    private final List<LineResponse> lines;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.customerId = order.getCustomerId();
        this.status = order.getStatus().name();
        this.total = order.getTotal();
        this.lines = order.getLines().stream().map(LineResponse::new).collect(Collectors.toList());
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public List<LineResponse> getLines() { return lines; }

    public static class LineResponse {
        private final UUID productId;
        private final String sku;
        private final String name;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal lineTotal;

        public LineResponse(OrderLine line) {
            this.productId = line.getProductId();
            this.sku = line.getSku();
            this.name = line.getName();
            this.quantity = line.getQuantity();
            this.unitPrice = line.getUnitPrice();
            this.lineTotal = line.getLineTotal();
        }

        public UUID getProductId() { return productId; }
        public String getSku() { return sku; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getLineTotal() { return lineTotal; }
    }
}
