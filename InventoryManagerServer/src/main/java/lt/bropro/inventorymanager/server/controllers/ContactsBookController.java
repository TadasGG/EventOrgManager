package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.bropro.inventorymanager.server.database.ContactsBookRepository;
import lt.bropro.inventorymanager.server.exceptions.ContactNotFoundException;
import lt.bropro.inventorymanager.server.exceptions.EmployeeNotFoundException;
import lt.bropro.inventorymanager.server.model.ContactsBook;
import lt.bropro.inventorymanager.server.model.Employee;
import lt.bropro.inventorymanager.server.model.StyleInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/contacts")
@Tag(name = "Contacts Book")
public class ContactsBookController {

    @Autowired
    private ContactsBookRepository contactsBookRepository;

    @Operation(
            summary = "Get all contacts book records.",
            description = "Returns all records in the contacts book."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all contacts.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactsBook.class))}),
            @ApiResponse(responseCode = "404", description = "Contacts not found.",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ContactsBook>> getAllContacts() {
        return ResponseEntity.ok(contactsBookRepository.findAll());
    }

    @Operation(
            summary = "Get a contact by ID",
            description = "Retrieves an contact record by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactsBook.class))}),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content)
    })
    @GetMapping("/{contactId}")
    public ResponseEntity<ContactsBook> getContactById(@PathVariable Long contactId) {
        Optional<ContactsBook> optionalContactsBook = contactsBookRepository.findById(contactId);
        if (optionalContactsBook.isPresent()) {
            ContactsBook contactsBook = optionalContactsBook.get();
            return ResponseEntity.ok().body(contactsBook);
        } else {
            throw new ContactNotFoundException(contactId);
        }
    }

    @Operation(
            summary = "Add a contact",
            description = "Add a new contact record."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactsBook.class))})
    })
    @PostMapping
    public ResponseEntity<ContactsBook> addContacts(@RequestBody ContactsBook contactsBook) {
        ContactsBook savedContactInformation = contactsBookRepository.save(contactsBook);
        return ResponseEntity.ok().body(savedContactInformation);
    }

    @Operation(
            summary = "Update a contact",
            description = "Update an existing contact record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactsBook.class))}),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content)
    })
    @PutMapping("/{contactId}")
    public ResponseEntity<ContactsBook> updateContact(
            @PathVariable Long contactId,
            @RequestBody ContactsBook contactsBookDetails) {

        Optional<ContactsBook> optionalContactsBook = contactsBookRepository.findById(contactId);
        if (optionalContactsBook.isPresent()) {
            ContactsBook contactsBook = optionalContactsBook.get();
            contactsBook.setCompanyName(contactsBookDetails.getCompanyName());
            contactsBook.setCompanyNumber(contactsBookDetails.getCompanyNumber());
            contactsBook.setCompanyAddress(contactsBookDetails.getCompanyAddress());
            contactsBook.setCompanyCity(contactsBookDetails.getCompanyCity());
            contactsBook.setCompanyCountry(contactsBookDetails.getCompanyCountry());
            contactsBook.setCompanyVatNumber(contactsBookDetails.getCompanyVatNumber());

            ContactsBook updatedContactsBook = contactsBookRepository.save(contactsBook);
            return ResponseEntity.ok(updatedContactsBook);
        } else {
            throw new ContactNotFoundException(contactId);
        }
    }

    @Operation(
            summary = "Delete a contact",
            description = "Delete an existing contact record based on the provided contact ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contact was deleted.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactsBook.class))})
    })
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long contactId) {
        Optional<ContactsBook> optionalContactsBook = contactsBookRepository.findById(contactId);
        if (optionalContactsBook.isPresent()) {
            ContactsBook contactsBook = optionalContactsBook.get();
            contactsBookRepository.delete(contactsBook);
            return ResponseEntity.noContent().build();
        } else {
            throw new ContactNotFoundException(contactId);
        }
    }
}
