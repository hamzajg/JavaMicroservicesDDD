package Order.App.src.main.java.Order.App.Ports;

import java.util.Optional;
import java.util.UUID;

public interface CustomerDirectory {

    boolean customerExists(UUID customerId);

    /** The id of the customer whose email matches, if any — caller→customer resolution. */
    Optional<UUID> findByEmail(String email);
}
