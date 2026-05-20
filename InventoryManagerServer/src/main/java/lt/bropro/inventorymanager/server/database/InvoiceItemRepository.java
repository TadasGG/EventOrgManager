package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.Invoice;
import lt.bropro.inventorymanager.server.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
