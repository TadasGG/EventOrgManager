package lt.bropro.inventorymanager.server.exceptions;

public class ItemCategoryNotFoundException extends RuntimeException {
    public ItemCategoryNotFoundException(Long id) {
        super("Could not find item category" + id);
    }
}
