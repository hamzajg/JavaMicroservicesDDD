package Product.App.src.main.java.Product.App.UseCases;

import Services.Common.src.main.java.Services.Common.Commands.ICommand;
import java.math.BigDecimal;

public class RegisterProduct implements ICommand {

    private String sku;
    private String name;
    private String description;
    private BigDecimal price;

    public RegisterProduct(String sku, String name, String description, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
