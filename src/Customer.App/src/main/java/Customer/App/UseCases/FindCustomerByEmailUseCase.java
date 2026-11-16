package Customer.App.src.main.java.Customer.App.UseCases;

import Customer.App.src.main.java.Customer.App.Repositories.CustomerRepository;
import Customer.App.src.main.java.Customer.Domain.Customer;
import java.util.Optional;

public class FindCustomerByEmailUseCase {

    private final CustomerRepository customerRepository;

    public FindCustomerByEmailUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> find(String email) {
        if (email == null || email.isBlank())
            return Optional.empty();
        return customerRepository.findByEmail(email);
    }
}
