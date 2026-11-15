package Identity.App.src.main.java.Identity.App.UseCases;

public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
