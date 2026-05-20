package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Invoice findTopByOrderByInvoiceNumberDesc();

}
