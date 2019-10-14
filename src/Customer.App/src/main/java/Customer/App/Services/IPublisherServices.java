package Customer.App.src.main.java.Customer.App.Services;

import Services.Common.src.main.java.Services.Common.Domain.Events.DomainEvent;

public interface IPublisherServices {
    
    void publish(DomainEvent event);

}