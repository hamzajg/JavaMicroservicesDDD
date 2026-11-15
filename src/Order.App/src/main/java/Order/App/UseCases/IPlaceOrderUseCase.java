package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.Domain.Order;
import Services.Common.src.main.java.Services.Common.Commands.ICommandHandler;

public interface IPlaceOrderUseCase extends ICommandHandler<PlaceOrder> {

    Order place(PlaceOrder command) throws Exception;
}
