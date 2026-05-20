package lt.bropro.inventorymanager.server.exceptions;

public class EventTypeNotFoundException extends RuntimeException {
    public EventTypeNotFoundException(Long id) {
        super("Could not find event type" + id);
    }
}
