package Customer.App.src.main.java.Customer.App.Repositories;

import Customer.App.src.main.java.Customer.Domain.Customer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<UUID, Customer> customers = new HashMap<>();

    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> findById(UUID id) {
        return Optional.ofNullable(customers.get(id));
    }

    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }
}
