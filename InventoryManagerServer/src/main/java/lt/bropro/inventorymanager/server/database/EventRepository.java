package lt.bropro.inventorymanager.server.database;

import lt.bropro.inventorymanager.server.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByInvoiceList_Id(Long invoiceId);
    List<Event> findAllByDate(LocalDate date);
}
