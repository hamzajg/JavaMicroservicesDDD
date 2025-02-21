package Services.Common.src.main.java.Services.Common.Domain.Events;
import java.time.LocalDateTime;
import java.util.UUID;
public abstract class DomainEvent implements IEvent {
    
    private UUID id = UUID.randomUUID();

    private int version;
    
    private LocalDateTime ocurredOn;

    protected DomainEvent() {
        ocurredOn = LocalDateTime.now();
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    public LocalDateTime getOcurredOn() {
        return ocurredOn;
    }

    public UUID getId() {
        return id;
    }

}