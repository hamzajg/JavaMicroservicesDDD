package com.hamzajg.customer.api;

import Customer.App.src.main.java.Customer.Domain.Customer;
import java.util.UUID;

public class CustomerResponse {

    private final UUID id;
    private final String fullName;
    private final String email;
    private final String address;
    private final String phoneNumber;

    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.fullName = customer.getFullName().getFullName();
        this.email = customer.getEmail().getEmail();
        this.address = customer.getAddress().getAddress();
        this.phoneNumber = customer.getPhoneNumber().getPhoneNumber();
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
}
