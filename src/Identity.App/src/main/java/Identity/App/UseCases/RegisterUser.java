package Identity.App.src.main.java.Identity.App.UseCases;

import Services.Common.src.main.java.Services.Common.Commands.ICommand;

public class RegisterUser implements ICommand {

    private String email;
    private String displayName;
    private String password;

    public RegisterUser(String email, String displayName, String password) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }
}
