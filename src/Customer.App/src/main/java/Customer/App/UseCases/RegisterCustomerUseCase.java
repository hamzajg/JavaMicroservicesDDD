package Customer.App.src.main.java.Customer.App.UseCases;

import Customer.App.src.main.java.Customer.App.Repositories.CustomerRepository;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import Customer.App.src.main.java.Customer.Domain.*;
import Customer.App.src.main.java.Customer.Events.CustomerCreated;

public class RegisterCustomerUseCase implements IRegisterCustomerUseCase {

    private final IPublisherServices pubServices;
    private final CustomerRepository customerRepository;

    public RegisterCustomerUseCase(IPublisherServices pubServices, CustomerRepository customerRepository) {
        this.pubServices = pubServices;
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer register(RegisterCustomer command) throws Exception {
        validate(command);
        Customer customer = new Customer(new FullName(command.getFirstName(), command.getLastName()),
        new Email(command.getEmail()), new Address(command.getAddress()), new PhoneNumber(command.getPhoneNumber()));
        customerRepository.save(customer);
        customer.getDomainEvents().forEach(pubServices::publish);
        return customer;
    }

    @Override
    public void handle(RegisterCustomer command) throws Exception {
        register(command);
    }

    private void validate(RegisterCustomer command) {
        if (command == null)
            throw new IllegalArgumentException("command must not be null");
        if (isBlank(command.getFirstName()) || isBlank(command.getLastName()))
            throw new IllegalArgumentException("first and last name are required");
        if (command.getEmail() == null || !command.getEmail().contains("@"))
            throw new IllegalArgumentException("a valid email is required");
        if (isBlank(command.getAddress()) || isBlank(command.getPhoneNumber()))
            throw new IllegalArgumentException("address and phone number are required");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
