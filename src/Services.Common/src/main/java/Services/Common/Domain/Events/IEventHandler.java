package Services.Common.src.main.java.Services.Common.Domain.Events;

public interface IEventHandler<TEvent extends IEvent> {
    void handle(TEvent event);
}