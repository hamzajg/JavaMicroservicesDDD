package Order.App.src.main.java.Order.App.UseCases;

public class InvalidOrderStateException extends Exception {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
