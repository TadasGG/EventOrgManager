package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.bropro.inventorymanager.server.database.CompanyInformationRepository;
import lt.bropro.inventorymanager.server.exceptions.ContactNotFoundException;
import lt.bropro.inventorymanager.server.model.CompanyInformation;
import lt.bropro.inventorymanager.server.model.ContactsBook;
import lt.bropro.inventorymanager.server.model.StyleInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/company-info")
@Tag(name = "Company Information")
public class CompanyInformationController {

    @Autowired
    private CompanyInformationRepository companyInformationRepository;

    @Operation(
            summary = "Get all company information records.",
            description = "Returns all records of the company."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all information.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyInformation.class))}),
            @ApiResponse(responseCode = "404", description = "Information not found.",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<CompanyInformation> getAllInformation() {
        List<CompanyInformation> all = companyInformationRepository.findAll();
        if (all.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(all.get(0));
    }

    @Operation(
            summary = "Get information by ID",
            description = "Retrieves the company information record by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyInformation.class))}),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content)
    })
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyInformation> getInformationById(@PathVariable Long companyId) {
        Optional<CompanyInformation> optionalCompanyInformation = companyInformationRepository.findById(companyId);
        if (optionalCompanyInformation.isPresent()) {
            CompanyInformation companyInformation = optionalCompanyInformation.get();
            return ResponseEntity.ok().body(companyInformation);
        } else {
            throw new ContactNotFoundException(companyId);
        }
    }
    @Operation(
            summary = "Update the information",
            description = "Update existing company information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyInformation.class))}),
            @ApiResponse(responseCode = "404", description = "Information not found",
                    content = @Content)
    })
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyInformation> updateInformation(
            @PathVariable Long companyId,
            @RequestBody CompanyInformation companyInformation ) {

        Optional<CompanyInformation> optionalCompanyInformation = companyInformationRepository.findById(companyId);
        if (optionalCompanyInformation.isPresent()) {
            CompanyInformation updatedCompanyInformation = optionalCompanyInformation.get();
            updatedCompanyInformation.setCompanyName(companyInformation.getCompanyName());
            updatedCompanyInformation.setCompanyNumber(companyInformation.getCompanyNumber());
            updatedCompanyInformation.setCompanyAddress(companyInformation.getCompanyAddress());
            updatedCompanyInformation.setCompanyCity(companyInformation.getCompanyCity());
            updatedCompanyInformation.setCompanyCountry(companyInformation.getCompanyCountry());
            updatedCompanyInformation.setCompanyVatNumber(companyInformation.getCompanyVatNumber());

            updatedCompanyInformation.setBankCode(companyInformation.getBankCode());
            updatedCompanyInformation.setBankAccountCode(companyInformation.getBankAccountCode());

            CompanyInformation savedCompanyInformation = companyInformationRepository.save(updatedCompanyInformation);
            return ResponseEntity.ok(savedCompanyInformation);
        } else {
            throw new ContactNotFoundException(companyId);
        }
    }
}
