package Services.Common.src.main.java.Services.Common.Commands;

public interface ICommandHandler<TCommand extends ICommand> {
    void handle(TCommand command);
}