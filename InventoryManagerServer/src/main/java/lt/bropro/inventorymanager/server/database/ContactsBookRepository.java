package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.ContactsBook;
import lt.bropro.inventorymanager.server.model.StyleInformation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Bill entities.
 *
 * Provides basic CRUD operations for Bill entities by extending JpaRepository.
 */
public interface ContactsBookRepository extends JpaRepository<ContactsBook, Long> {

}
