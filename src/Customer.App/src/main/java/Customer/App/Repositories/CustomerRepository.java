package Customer.App.src.main.java.Customer.App.Repositories;

import Customer.App.src.main.java.Customer.Domain.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    void save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();
}
