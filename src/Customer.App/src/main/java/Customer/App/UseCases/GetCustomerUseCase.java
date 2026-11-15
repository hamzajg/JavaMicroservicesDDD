package Customer.App.src.main.java.Customer.App.UseCases;

import Customer.App.src.main.java.Customer.App.Repositories.CustomerRepository;
import Customer.App.src.main.java.Customer.Domain.Customer;
import java.util.Optional;
import java.util.UUID;

public class GetCustomerUseCase {

    private final CustomerRepository customerRepository;

    public GetCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> get(UUID id) {
        return customerRepository.findById(id);
    }
}
