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
import Order.App.src.main.java.Order.App.UseCases.ResolveCustomerUseCase;
import Order.App.src.main.java.Order.Domain.Order;
import com.hamzajg.order.api.auth.BearerAuthFilter;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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
    private final ResolveCustomerUseCase resolveCustomerUseCase;

    public OrderController(IPlaceOrderUseCase placeOrderUseCase,
                           ICancelOrderUseCase cancelOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           ListOrdersUseCase listOrdersUseCase,
                           ResolveCustomerUseCase resolveCustomerUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.resolveCustomerUseCase = resolveCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> place(HttpServletRequest request,
                                               @RequestBody PlaceOrderRequest body) {
        try {
            UUID customerId = callerCustomerId(request);
            List<PlaceOrder.OrderItem> items = body.getItems().stream()
                    .map(i -> new PlaceOrder.OrderItem(i.getProductId(), i.getQuantity()))
                    .collect(Collectors.toList());
            Order order = placeOrderUseCase.place(new PlaceOrder(customerId, items));
            return ResponseEntity.created(URI.create("/orders/" + order.getId()))
                    .body(new OrderResponse(order));
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UpstreamException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatus()), e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public List<OrderResponse> list(HttpServletRequest request) {
        return listOrdersUseCase.listFor(callerCustomerId(request)).stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id, HttpServletRequest request) {
        return new OrderResponse(ownedOrder(id, callerCustomerId(request)));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable UUID id, HttpServletRequest request) {
        Order order = ownedOrder(id, callerCustomerId(request));
        try {
            return new OrderResponse(cancelOrderUseCase.cancel(new CancelOrder(order.getId())));
        } catch (OrderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidOrderStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /** Resolves the authenticated caller (by token email) to the customer owning their orders. */
    private UUID callerCustomerId(HttpServletRequest request) {
        Object email = request.getAttribute(BearerAuthFilter.CALLER_EMAIL_ATTR);
        return resolveCustomerUseCase.resolveByEmail(email == null ? null : email.toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no customer for caller"));
    }

    /** Loads an order only if it belongs to the caller; foreign orders are 404, not 403. */
    private Order ownedOrder(UUID id, UUID callerCustomerId) {
        return getOrderUseCase.get(id)
                .filter(order -> callerCustomerId.equals(order.getCustomerId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
    }
}
