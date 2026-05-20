package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.StyleInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleInformationRepository extends JpaRepository<StyleInformation, Long> {
    StyleInformation findFirstByOrderByIdAsc();
}
