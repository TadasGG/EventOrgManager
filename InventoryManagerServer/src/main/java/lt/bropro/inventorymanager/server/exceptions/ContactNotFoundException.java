package lt.bropro.inventorymanager.server.exceptions;

public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(Long id) {
        super("Could not find contact " + id);
    }
}
