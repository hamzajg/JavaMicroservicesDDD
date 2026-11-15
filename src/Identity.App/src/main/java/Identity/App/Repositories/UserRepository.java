package Identity.App.src.main.java.Identity.App.Repositories;

import Identity.App.src.main.java.Identity.Domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);
}
