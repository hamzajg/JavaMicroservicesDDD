package Order.App.src.main.java.Order.App.UseCases;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
