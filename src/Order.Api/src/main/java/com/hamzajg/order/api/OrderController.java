package com.hamzajg.order.api;

import Order.App.src.main.java.Order.App.UseCases.CancelOrder;
import Order.App.src.main.java.Order.App.UseCases.CustomerNotFoundException;
import Order.App.src.main.java.Order.App.UseCases.GetOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.ICancelOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.IPlaceOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.InvalidOrderStateException;
import Order.App.src.main.java.Order.App.UseCases.ListOrdersUseCase;
import Order.App.src.main.java.Order.App.UseCases.OrderNotFoundException;
import Order.App.src.main.java.Order.App.UseCases.PlaceOrder;
import Order.App.src.main.java.Order.App.UseCases.ProductNotFoundException;
import Order.App.src.main.java.Order.Domain.Order;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final IPlaceOrderUseCase placeOrderUseCase;
    private final ICancelOrderUseCase cancelOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;

    public OrderController(IPlaceOrderUseCase placeOrderUseCase,
                           ICancelOrderUseCase cancelOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           ListOrdersUseCase listOrdersUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@RequestBody PlaceOrderRequest request) {
        try {
            List<PlaceOrder.OrderItem> items = request.getItems().stream()
                    .map(i -> new PlaceOrder.OrderItem(i.getProductId(), i.getQuantity()))
                    .collect(Collectors.toList());
            Order order = placeOrderUseCase.place(new PlaceOrder(request.getCustomerId(), items));
            return ResponseEntity.created(URI.create("/orders/" + order.getId()))
                    .body(new OrderResponse(order));
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public List<OrderResponse> list() {
        return listOrdersUseCase.list().stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return getOrderUseCase.get(id)
                .map(OrderResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable UUID id) {
        try {
            return new OrderResponse(cancelOrderUseCase.cancel(new CancelOrder(id)));
        } catch (OrderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidOrderStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
