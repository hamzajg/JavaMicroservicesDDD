package Identity.App.src.main.java.Identity.Events;

import Identity.App.src.main.java.Identity.Domain.*;
import Services.Common.src.main.java.Services.Common.Domain.Events.*;

public class UserRegistered extends DomainEvent {

    private User user;

    public UserRegistered(User user) {
        this.user = user;
    }

}
