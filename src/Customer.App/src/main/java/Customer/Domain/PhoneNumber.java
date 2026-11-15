package Customer.App.src.main.java.Customer.Domain;

public class PhoneNumber {
    private String phoneNumer;

    public PhoneNumber(String phoneNumber){
        this.phoneNumer = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumer;
    }
}