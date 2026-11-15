package Product.App.src.main.java.Product.App.UseCases;

import Product.App.src.main.java.Product.Domain.Product;
import Services.Common.src.main.java.Services.Common.Commands.ICommandHandler;

public interface IRegisterProductUseCase extends ICommandHandler<RegisterProduct> {

    Product register(RegisterProduct command) throws Exception;
}
