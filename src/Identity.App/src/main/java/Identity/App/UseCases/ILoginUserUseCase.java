package Identity.App.src.main.java.Identity.App.UseCases;

import Identity.App.src.main.java.Identity.App.Services.AuthToken;
import Services.Common.src.main.java.Services.Common.Commands.ICommandHandler;

public interface ILoginUserUseCase extends ICommandHandler<LoginUser> {

    AuthToken login(LoginUser command) throws Exception;
}
