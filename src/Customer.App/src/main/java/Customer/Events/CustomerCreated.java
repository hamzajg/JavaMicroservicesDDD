package Customer.App.src.main.java.Customer.Events;

import Customer.App.src.main.java.Customer.Domain.*;
import Services.Common.src.main.java.Services.Common.Domain.Events.*;

public class CustomerCreated extends DomainEvent {

    private Customer customer;

    public CustomerCreated(Customer customer) {
        this.customer = customer;
    }
    
}