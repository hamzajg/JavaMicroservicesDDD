package Identity.App.src.main.java.Identity.App.UseCases;

public class DuplicateEmailException extends Exception {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
