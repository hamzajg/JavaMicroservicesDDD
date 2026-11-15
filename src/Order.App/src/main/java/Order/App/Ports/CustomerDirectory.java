package Order.App.src.main.java.Order.App.Ports;

import java.util.UUID;

public interface CustomerDirectory {

    boolean customerExists(UUID customerId);
}
