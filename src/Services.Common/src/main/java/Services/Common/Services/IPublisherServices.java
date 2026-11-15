package Services.Common.src.main.java.Services.Common.Services;

import Services.Common.src.main.java.Services.Common.Domain.Events.DomainEvent;

public interface IPublisherServices {
    
    void publish(DomainEvent event);

}