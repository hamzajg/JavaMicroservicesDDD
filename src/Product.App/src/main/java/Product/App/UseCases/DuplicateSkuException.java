package Product.App.src.main.java.Product.App.UseCases;

public class DuplicateSkuException extends Exception {

    public DuplicateSkuException(String message) {
        super(message);
    }
}
