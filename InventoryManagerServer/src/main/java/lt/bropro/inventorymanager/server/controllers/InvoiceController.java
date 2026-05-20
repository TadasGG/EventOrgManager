package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lt.bropro.inventorymanager.server.database.ContactsBookRepository;
import lt.bropro.inventorymanager.server.database.InvoiceRepository;
import lt.bropro.inventorymanager.server.database.EventRepository;
import lt.bropro.inventorymanager.server.exceptions.InvoiceNotFoundException;
import lt.bropro.inventorymanager.server.exceptions.EventNotFoundException;
import lt.bropro.inventorymanager.server.model.ContactsBook;
import lt.bropro.inventorymanager.server.model.Invoice;
import lt.bropro.inventorymanager.server.model.InvoiceItem;
import lt.bropro.inventorymanager.server.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ContactsBookRepository contactsBookRepository;

    @Operation(
            summary = "Get all invoices",
            description = "Returns a list of all invoices"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoices found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))})
    })
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return ResponseEntity.ok(invoices);
    }

    @Operation(
            summary = "Get invoice by ID",
            description = "Returns a single invoice for the given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        return ResponseEntity.ok(invoice);
    }

    @Operation(
            summary = "Create a new invoice",
            description = "Creates a new invoice and (optionally) attaches it to an event"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))})
    })
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody Invoice invoiceRequest,
            @RequestParam(name = "eventId", required = false) Long eventId
    ) {
        ContactsBook incoming = invoiceRequest.getContactsBook();
        if (incoming != null) {
            if (incoming.getId() != 0) {
                ContactsBook managed = contactsBookRepository.findById((long) incoming.getId())
                        .orElseThrow(() ->
                                new RuntimeException("ContactsBook not found with id " + incoming.getId()));
                invoiceRequest.setContactsBook(managed);
            } else {
                ContactsBook savedContact = contactsBookRepository.save(incoming);
                invoiceRequest.setContactsBook(savedContact);
            }
        }

        Invoice last = invoiceRepository.findTopByOrderByInvoiceNumberDesc();
        int nextNumber = (last != null) ? last.getInvoiceNumber() + 1 : 1;
        invoiceRequest.setInvoiceNumber(nextNumber);

        if (eventId != null) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException(eventId));

            if (event.getInvoiceList() == null) {
                event.setInvoiceList(new ArrayList<>());
            }

            event.getInvoiceList().add(invoiceRequest);
        }

        Invoice saved = invoiceRepository.save(invoiceRequest);
        return ResponseEntity.ok(saved);
    }

    @Operation(
            summary = "Get the next invoice number",
            description = "Returns the next invoice number"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice number found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice number not found",
                    content = @Content)
    })
    @GetMapping("/next-number")
    public ResponseEntity<Integer> getNextInvoiceNumber() {
        Invoice last = invoiceRepository.findTopByOrderByInvoiceNumberDesc();

        int nextNumber = (last != null) ? last.getInvoiceNumber() + 1 : 1;

        return ResponseEntity.ok(nextNumber);
    }

    @Operation(
            summary = "Update an invoice",
            description = "Updates an existing invoice by ID (header info and status)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(
            @PathVariable Long id,
            @RequestBody Invoice invoiceDetails) {

        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        existing.setInvoiceNumber(invoiceDetails.getInvoiceNumber());
        existing.setDate(invoiceDetails.getDate());
        existing.setInvoiceStatus(invoiceDetails.getInvoiceStatus());

        ContactsBook incoming = invoiceDetails.getContactsBook();
        if (incoming != null) {
            if (incoming.getId() != 0) {
                ContactsBook managed = contactsBookRepository
                        .findById((long) incoming.getId())
                        .orElseThrow(() ->
                                new RuntimeException("ContactsBook not found with id " + incoming.getId()));
                existing.setContactsBook(managed);
            } else {
                ContactsBook saved = contactsBookRepository.save(incoming);
                existing.setContactsBook(saved);
            }
        } else {
            existing.setContactsBook(null);
        }

        Invoice updated = invoiceRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete an invoice",
            description = "Deletes an invoice by ID (and its items)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Invoice not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        List<Event> events = eventRepository.findByInvoiceList_Id(id);

        for (Event ev : events) {
            ev.getInvoiceList().removeIf(inv -> inv.getId() == id);
        }

        eventRepository.saveAll(events);

        invoiceRepository.delete(existing);

        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Get all invoice items for an invoice",
            description = "Returns a list of all items in a given invoice"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice items found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InvoiceItem.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice not found",
                    content = @Content)
    })
    @GetMapping("/{invoiceId}/records")
    public ResponseEntity<List<InvoiceItem>> getAllInvoiceItems(@PathVariable Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        return ResponseEntity.ok(invoice.getInvoiceItemList());
    }

    @Operation(
            summary = "Add a new item to an invoice",
            description = "Creates a new invoice item and attaches it to the given invoice"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice item added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice not found",
                    content = @Content)
    })
    @PostMapping("/{invoiceId}/records")
    public ResponseEntity<Invoice> addInvoiceItem(
            @PathVariable Long invoiceId,
            @RequestBody InvoiceItem itemRequest) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        if (invoice.getInvoiceItemList() == null) {
            invoice.setInvoiceItemList(new ArrayList<>());
        }

        invoice.getInvoiceItemList().add(itemRequest);

        Invoice updated = invoiceRepository.save(invoice);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Update an invoice item",
            description = "Updates an existing invoice item inside a specific invoice"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice item updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice or item not found",
                    content = @Content)
    })
    @PutMapping("/{invoiceId}/records/{itemId}")
    public ResponseEntity<Invoice> updateInvoiceItem(
            @PathVariable Long invoiceId,
            @PathVariable Long itemId,
            @RequestBody InvoiceItem itemDetails) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        InvoiceItem existingItem = invoice.getInvoiceItemList().stream()
                .filter(it -> it.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invoice item not found: " + itemId));

        existingItem.setService(itemDetails.getService());
        existingItem.setMeasure(itemDetails.getMeasure());
        existingItem.setPieces(itemDetails.getPieces());
        existingItem.setCost(itemDetails.getCost());

        Invoice updated = invoiceRepository.save(invoice);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete an invoice item",
            description = "Removes an invoice item from an invoice and deletes it"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice item deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice or item not found",
                    content = @Content)
    })
    @DeleteMapping("/{invoiceId}/records/{itemId}")
    public ResponseEntity<Invoice> deleteInvoiceItem(
            @PathVariable Long invoiceId,
            @PathVariable Long itemId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        boolean removed = invoice.getInvoiceItemList().removeIf(it -> it.getId() == itemId);
        if (!removed) {
            throw new RuntimeException("Invoice item not found: " + itemId);
        }

        Invoice updated = invoiceRepository.save(invoice);
        return ResponseEntity.ok(updated);
    }
}