package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.App.Ports.CustomerDirectory;
import java.util.Optional;
import java.util.UUID;

/**
 * Resolves an authenticated caller (identified by their Identity email) to the
 * customer that owns their orders. This is the seam that lets `/orders` scope
 * work to the caller instead of trusting a client-supplied customerId.
 */
public class ResolveCustomerUseCase {

    private final CustomerDirectory customerDirectory;

    public ResolveCustomerUseCase(CustomerDirectory customerDirectory) {
        this.customerDirectory = customerDirectory;
    }

    public Optional<UUID> resolveByEmail(String email) {
        if (email == null || email.isBlank())
            return Optional.empty();
        return customerDirectory.findByEmail(email);
    }
}
