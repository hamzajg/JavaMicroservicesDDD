package Customer.App.src.main.java.Customer.App.UseCases;

import Customer.App.src.main.java.Customer.App.Services.IPublisherServices;
import Customer.App.src.main.java.Customer.Domain.*;
import Customer.App.src.main.java.Customer.Events.CustomerCreated;

public class RegisterCustomerUseCase implements IRegisterCustomerUseCase {

    private IPublisherServices pubServices;

    public RegisterCustomerUseCase(IPublisherServices pubServices) {
        this.pubServices = pubServices;
    }

    public void handle(RegisterCustomer command) throws Exception {
        if (command == null)
            throw new Exception();
        Customer customer = new Customer(new FullName(command.getFirstName(), command.getLastName()),
        new Email(command.getEmail()), new Address(command.getAddress()), new PhoneNumber(command.getPhoneNumber()));
        pubServices.publish(new CustomerCreated(customer));
    }
}