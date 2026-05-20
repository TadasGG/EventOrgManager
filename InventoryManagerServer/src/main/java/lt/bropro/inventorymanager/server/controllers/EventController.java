package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.bropro.inventorymanager.server.database.*;
import lt.bropro.inventorymanager.server.database.EventRepository;
import lt.bropro.inventorymanager.server.exceptions.EmployeeNotFoundException;
import lt.bropro.inventorymanager.server.exceptions.EventNotFoundException;
import lt.bropro.inventorymanager.server.exceptions.InvoiceNotFoundException;
import lt.bropro.inventorymanager.server.exceptions.ItemNotFoundException;
import lt.bropro.inventorymanager.server.model.*;
import lt.bropro.inventorymanager.server.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin/events")
@Tag(name = "Event Controller")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StoredItemsRepository storedItemsRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Operation(
            summary = "Get all events.",
            description = "Returns all events."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all events.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Event not found.",
                    content = @Content)
    })
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Operation(
            summary = "Get an event by ID",
            description = "Retrieves an event by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content)
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            return ResponseEntity.ok().body(event);
        } else {
            throw new EventNotFoundException(eventId);
        }
    }

    @Operation(
            summary = "Get an event by date",
            description = "Retrieves an event by a specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content)
    })
    @GetMapping("/date/{date}")
    public List<Event> getEventByDate(@PathVariable LocalDate date) {
        return eventRepository.findAllByDate(date);
    }
    @Operation(
            summary = "Add an event",
            description = "Add a new event"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))})
    })
    @PostMapping
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @Operation(
            summary = "Update an event by ID",
            description = "Update an existing event based on the provided event ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content)
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody Event eventDetails) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setAddress(eventDetails.getAddress());
            event.setDate(eventDetails.getDate());
            event.setArrivalTime(eventDetails.getArrivalTime());
            event.setMeetupTime(eventDetails.getMeetupTime());
            event.setGuestTime(eventDetails.getGuestTime());
            event.setEndTime(eventDetails.getEndTime());
            event.setReadyTime(eventDetails.getReadyTime());
            event.setSoundCheckTime(eventDetails.getSoundCheckTime());
            event.setStartTime(eventDetails.getStartTime());
            event.setComments(eventDetails.getComments());
            event.setTitle(eventDetails.getTitle());

            Event updatedEvent = eventRepository.save(event);
            return ResponseEntity.ok(updatedEvent);
        } else {
            throw new EventNotFoundException(eventId);
        }
    }

    @Operation(
            summary = "Delete an event",
            description = "Delete an existing event based on the provided event ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event was deleted.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))})
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            eventRepository.delete(event);
            return ResponseEntity.noContent().build();
        } else {
            throw new EventNotFoundException(eventId);
        }
    }

    @Operation(
            summary = "Get all employees for an event",
            description = "Returns the list of employees assigned to the specified event"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content)
    })
    @GetMapping("/{eventId}/employees")
    public ResponseEntity<Set<Employee>> getEmployeesForEvent(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Set<Employee> employees = event.getEmployeeList();
        return ResponseEntity.ok(employees);
    }

    @Operation(
            summary = "Add or remove employees for events",
            description = "Adds or removes employees to/from an event based on the provided employee ID and event IDs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event's employees updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Employee or Event not found",
                    content = @Content)
    })
    @PutMapping("/{eventId}/employees")
    public ResponseEntity<Event> updateEventEmployees(
            @PathVariable Long eventId,
            @RequestParam List<Long> employeeId,
            @RequestParam(defaultValue = "false") boolean removeEmployees) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getEmployeeList() == null) {
            event.setEmployeeList(new HashSet<>());
        }

        Set<Employee> employees = new HashSet<>(employeeRepository.findAllById(employeeId));
        if (employees.size() != employeeId.size()) {
            throw new EmployeeNotFoundException(null);
        }

        if (removeEmployees) {
            event.getEmployeeList().removeAll(employees);
        } else {
            event.getEmployeeList().addAll(employees);
        }

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(
            summary = "Get all items for an event",
            description = "Returns the list of items assigned to the specified event"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content)
    })
    @GetMapping("/{eventId}/items")
    public ResponseEntity<List<StoredItems>> getItemsForEvent(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<StoredItems> storedItems = event.getStoredItemsList();
        return ResponseEntity.ok(storedItems);
    }

    @Operation(
            summary = "Add or remove items to/from events",
            description = "Adds or removes items to/from an event based on the provided item IDs and event ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event's item updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Item or Event not found",
                    content = @Content)
    })
    @PutMapping("/{eventId}/items")
    public ResponseEntity<Event> updateEventItems(
            @PathVariable Long eventId,
            @RequestParam Long itemId,
            @RequestParam String amount,
            @RequestParam(defaultValue = "false") boolean removeItems) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<StoredItems> eventItems = event.getStoredItemsList();

        if (removeItems) {
            int targetAmount = amount.equalsIgnoreCase("all") ? Integer.MAX_VALUE : Integer.parseInt(amount);
            int removedCount = 0;

            Iterator<StoredItems> iterator = eventItems.iterator();
            while (iterator.hasNext() && removedCount < targetAmount) {
                StoredItems si = iterator.next();
                if (si.getItem() != null && si.getItem().getId() == itemId) {
                    iterator.remove();
                    if (Boolean.TRUE.equals(si.getExternalRental())) {
                        storedItemsRepository.delete(si);
                    } else {
                        si.setAvailability(true);
                        storedItemsRepository.save(si);
                    }
                    removedCount++;
                }
            }

        } else {
            int addAmount = Integer.parseInt(amount);
            List<StoredItems> availableItems = storedItemsRepository.findAll().stream()
                    .filter(si -> si.getItem() != null && si.getItem().getId() == itemId)
                    .filter(si -> !eventItems.contains(si))
                    .filter(StoredItems::getAvailability)
                    .limit(addAmount)
                    .toList();

            int actualAvailable = availableItems.size();

            for (StoredItems si : availableItems) {
                si.setAvailability(false);
                storedItemsRepository.save(si);
            }

            eventItems.addAll(availableItems);

            if (actualAvailable < addAmount) {
                Item item = availableItems.isEmpty()
                        ? itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId))
                        : availableItems.get(0).getItem();

                for (int i = 0; i < (addAmount - actualAvailable); i++) {
                    StoredItems externalItem = new StoredItems();
                    externalItem.setItem(item);
                    externalItem.setAvailability(false);
                    externalItem.setExternalRental(true);
                    storedItemsRepository.save(externalItem);
                    eventItems.add(externalItem);
                }
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(
            summary = "Remove specific items from events",
            description = "Removes specific items from an event based on the provided item IDs and event ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event's item(s) removed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Item or Event not found",
                    content = @Content)
    })
    @PutMapping("/{eventId}/items/remove-specific")
    public ResponseEntity<Event> removeSpecificItemsFromEvent(
            @PathVariable Long eventId,
            @RequestBody Map<String, List<Long>> payload) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<Long> storedItemIds = payload.get("storedItemIds");
        if (storedItemIds == null || storedItemIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<StoredItems> eventItems = event.getStoredItemsList();

        for (Long storedItemId : storedItemIds) {
            Optional<StoredItems> optionalItem = storedItemsRepository.findById(storedItemId);
            if (optionalItem.isEmpty()) continue;

            StoredItems si = optionalItem.get();
            if (!eventItems.contains(si)) continue;

            eventItems.remove(si);

            if (Boolean.TRUE.equals(si.getExternalRental())) {
                storedItemsRepository.delete(si);
            } else {
                si.setAvailability(true);
                storedItemsRepository.save(si);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(
            summary = "Get all invoices from event",
            description = "Lists all invoices from an event based on the provided invoice IDs and event ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event's bill updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))})
    })
    @GetMapping("/{eventId}/invoices")
    public ResponseEntity<List<Invoice>> getEventInvoices(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<Invoice> invoiceList = event.getInvoiceList();
        return ResponseEntity.ok(invoiceList);
    }

    @Operation(
            summary = "Add or remove invoices to/from events",
            description = "Adds or removes invoices to/from an event based on the provided invoice IDs and event ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event's invoices updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice or Event not found",
                    content = @Content)
    })
    @PutMapping("/{eventId}/invoices")
    public ResponseEntity<Event> updateEventInvoices(
            @PathVariable Long eventId,
            @RequestParam("invoiceId") List<Long> invoiceIds,
            @RequestParam(name = "removeInvoice", defaultValue = "false") boolean removeInvoice) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<Invoice> storedInvoices = invoiceRepository.findAllById(invoiceIds);

        if (storedInvoices.size() != invoiceIds.size()) {
            throw new InvoiceNotFoundException(null);
        }

        if (removeInvoice) {
            event.getInvoiceList().removeAll(storedInvoices);
        } else {
            for (Invoice invoice : storedInvoices) {
                if (!event.getInvoiceList().contains(invoice)) {
                    event.getInvoiceList().add(invoice);
                }
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(updatedEvent);
    }
}