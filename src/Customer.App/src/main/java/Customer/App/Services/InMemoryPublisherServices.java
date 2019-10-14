package Customer.App.src.main.java.Customer.App.Services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

import Services.Common.src.main.java.Services.Common.Domain.Events.DomainEvent;
import Services.Common.src.main.java.Services.Common.Domain.Events.IEvent;

public class InMemoryPublisherServices implements IPublisherServices {
    
    private Map<UUID, DomainEvent> events = new HashMap<UUID, DomainEvent>();


    public void publish(DomainEvent event) {
        System.err.println(event.getId().toString());
        events.put(event.getId(), event);
    }

    public ImmutableList<IEvent> getEvents() {
        return ImmutableList.copyOf(events.values());
    }

}