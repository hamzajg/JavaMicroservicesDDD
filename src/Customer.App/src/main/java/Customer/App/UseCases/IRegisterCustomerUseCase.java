package Customer.App.src.main.java.Customer.App.UseCases;

import Customer.App.src.main.java.Customer.Domain.Customer;
import Services.Common.src.main.java.Services.Common.Commands.ICommandHandler;

public interface IRegisterCustomerUseCase extends ICommandHandler<RegisterCustomer> {

    Customer register(RegisterCustomer command) throws Exception;
}
