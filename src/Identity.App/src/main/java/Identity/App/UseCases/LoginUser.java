package Identity.App.src.main.java.Identity.App.UseCases;

import Services.Common.src.main.java.Services.Common.Commands.ICommand;

public class LoginUser implements ICommand {

    private String email;
    private String password;

    public LoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
