package Customer.App.src.main.java.Customer.App.UseCases;

import Customer.App.src.main.java.Customer.App.Repositories.CustomerRepository;
import Customer.App.src.main.java.Customer.Domain.Customer;
import java.util.List;

public class ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    public ListCustomersUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }
}
