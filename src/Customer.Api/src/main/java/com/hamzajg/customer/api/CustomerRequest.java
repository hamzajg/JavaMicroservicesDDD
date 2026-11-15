package com.hamzajg.customer.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRequest {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String address;
    private final String phoneNumber;

    @JsonCreator
    public CustomerRequest(@JsonProperty("firstName") String firstName,
                           @JsonProperty("lastName") String lastName,
                           @JsonProperty("email") String email,
                           @JsonProperty("address") String address,
                           @JsonProperty("phoneNumber") String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
}
