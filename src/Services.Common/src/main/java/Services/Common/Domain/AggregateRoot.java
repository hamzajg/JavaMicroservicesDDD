package Services.Common.src.main.java.Services.Common.Domain;

import Services.Common.src.main.java.Services.Common.Domain.Events.*;
import com.google.common.collect.ImmutableList; 
import java.util.*;

import java.lang.Exception;

public abstract class AggregateRoot<T> extends Entity<T> {
    private List<DomainEvent> domainEvents = new ArrayList<>();

    private int version = 0;

    protected AggregateRoot() {
        
    }

    public void addDomainEvent(DomainEvent newEvent) throws InvalidVersionAggregateException {
        this.validateVersion(newEvent.getVersion());
        newEvent.setVersion(++version);
        domainEvents.add(newEvent);
    }

    private void validateVersion(int version) throws InvalidVersionAggregateException {
        if(this.version != version)
            throw new InvalidVersionAggregateException("Invalid version specified");
    }

    public ImmutableList<DomainEvent> getDomainEvents() {
        return ImmutableList.copyOf(domainEvents);
    }
    
}

class InvalidVersionAggregateException extends Exception {
    public InvalidVersionAggregateException(String message) {
        super(message);
    }
}
class AggregateException extends Exception {
    public AggregateException(String message) {
        super(message);
    }
}
class ArgumentNullAggregateException extends AggregateException {
    public ArgumentNullAggregateException(String message) {
        super(message);
    }
}