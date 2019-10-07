package Services.Common.src.main.java.Services.Common.Domain;

public abstract class Entity<T> {
    
    private T id;

    protected Entity() {
        
    }

    protected Entity(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

}