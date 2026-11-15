package Identity.App.src.main.java.Identity.App.UseCases;

import Identity.App.src.main.java.Identity.Domain.User;
import Services.Common.src.main.java.Services.Common.Commands.ICommandHandler;

public interface IRegisterUserUseCase extends ICommandHandler<RegisterUser> {

    User register(RegisterUser command) throws Exception;
}
