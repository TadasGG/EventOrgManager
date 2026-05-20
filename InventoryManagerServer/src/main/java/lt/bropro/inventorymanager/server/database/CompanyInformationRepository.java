package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.CompanyInformation;
import lt.bropro.inventorymanager.server.model.ContactsBook;
import lt.bropro.inventorymanager.server.model.StyleInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyInformationRepository extends JpaRepository<CompanyInformation, Long> {
    CompanyInformation findFirstByOrderByIdAsc();
}
