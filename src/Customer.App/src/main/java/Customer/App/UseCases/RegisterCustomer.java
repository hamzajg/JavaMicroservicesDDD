package Customer.App.src.main.java.Customer.App.UseCases;

import Services.Common.src.main.java.Services.Common.Commands.*;

public class RegisterCustomer implements ICommand {

    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;

    public RegisterCustomer(String firstName, String lastName, String address, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

}