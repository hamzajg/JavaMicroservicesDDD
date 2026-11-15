package com.hamzajg.customer.api;

import Customer.App.src.main.java.Customer.App.UseCases.GetCustomerUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.IRegisterCustomerUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.ListCustomersUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.RegisterCustomer;
import Customer.App.src.main.java.Customer.Domain.Customer;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final IRegisterCustomerUseCase registerCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;

    public CustomerController(IRegisterCustomerUseCase registerCustomerUseCase,
                              GetCustomerUseCase getCustomerUseCase,
                              ListCustomersUseCase listCustomersUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> register(@RequestBody CustomerRequest request) {
        try {
            Customer customer = registerCustomerUseCase.register(new RegisterCustomer(
                    request.getFirstName(), request.getLastName(), request.getAddress(),
                    request.getPhoneNumber(), request.getEmail()));
            return ResponseEntity.created(URI.create("/customers/" + customer.getId()))
                    .body(new CustomerResponse(customer));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return listCustomersUseCase.list().stream()
                .map(CustomerResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable UUID id) {
        return getCustomerUseCase.get(id)
                .map(CustomerResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));
    }
}
