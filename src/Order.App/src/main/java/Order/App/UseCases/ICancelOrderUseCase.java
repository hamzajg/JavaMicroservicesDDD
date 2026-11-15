package Order.App.src.main.java.Order.App.UseCases;

import Order.App.src.main.java.Order.Domain.Order;
import Services.Common.src.main.java.Services.Common.Commands.ICommandHandler;

public interface ICancelOrderUseCase extends ICommandHandler<CancelOrder> {

    Order cancel(CancelOrder command) throws Exception;
}
