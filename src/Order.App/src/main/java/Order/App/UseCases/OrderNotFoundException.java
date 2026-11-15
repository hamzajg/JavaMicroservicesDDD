package Order.App.src.main.java.Order.App.UseCases;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
