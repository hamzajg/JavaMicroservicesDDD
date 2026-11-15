package Identity.App.src.main.java.Identity.Domain;

import Identity.App.src.main.java.Identity.Events.*;
import Services.Common.src.main.java.Services.Common.Domain.*;
import java.util.UUID;

public class User extends AggregateRoot<UUID> {

    private String email;
    private String displayName;
    private String passwordHash;
    private String passwordSalt;

    protected User(){

    }

    public User(String email, String displayName, String passwordHash, String passwordSalt) {
        super(UUID.randomUUID());
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;

        try {
            addDomainEvent(new UserRegistered(this));
        }
        catch (Exception err) {
            System.out.println(err);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public boolean passwordMatches(String passwordHash) {
        return this.passwordHash.equals(passwordHash);
    }

}
