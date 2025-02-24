package Customer.App.src.main.java.Customer.Domain;

import Customer.App.src.main.java.Customer.Events.*;
import Services.Common.src.main.java.Services.Common.Domain.*;
import java.util.UUID;

public class Customer extends AggregateRoot<UUID> {
    
    private FullName fullName;
    private Email email;
    private Address address;
    private PhoneNumber phoneNumber;

    protected Customer(){

    }

    public Customer(FullName fullName, Email email, Address address, PhoneNumber phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;

        try {
            addDomainEvent(new CustomerCreated(this));
        }
        catch (Exception err) {
            System.out.println(err);
        }
    }


    public FullName getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

}