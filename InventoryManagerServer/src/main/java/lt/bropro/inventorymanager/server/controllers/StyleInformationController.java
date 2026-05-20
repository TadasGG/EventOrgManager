package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.bropro.inventorymanager.server.database.StyleInformationRepository;
import lt.bropro.inventorymanager.server.exceptions.EmployeeNotFoundException;
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
@RequestMapping("/public/styles")
@Tag(name = "Web Style Controller")
public class StyleInformationController {

    @Autowired
    private StyleInformationRepository styleInformationRepository;

    @Operation(
            summary = "Get all style information.",
            description = "Returns all style values."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all styles.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StyleInformation.class))}),
            @ApiResponse(responseCode = "404", description = "Styles not found.",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<StyleInformation> getFirstStyle() {
        List<StyleInformation> all = styleInformationRepository.findAll();
        if (all.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(all.get(0));
    }

    @Operation(
            summary = "Add a style",
            description = "Add a new style"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Style added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StyleInformation.class))})
    })
    @PostMapping
    public ResponseEntity<StyleInformation> addStyles(@RequestBody StyleInformation styleInformation) {
        StyleInformation savedStyleInformation = styleInformationRepository.save(styleInformation);
        return ResponseEntity.ok().body(savedStyleInformation);
    }

    @Operation(
            summary = "Update a style",
            description = "Update an existing style"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Style updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StyleInformation.class))}),
            @ApiResponse(responseCode = "404", description = "Style not found",
                    content = @Content)
    })
    @PutMapping("/{styleId}")
    public ResponseEntity<StyleInformation> updateStyle(
            @PathVariable Long styleId,
            @RequestBody StyleInformation styleInformationDetails) {

        Optional<StyleInformation> optionalStyleInformation = styleInformationRepository.findById(styleId);
        if (optionalStyleInformation.isPresent()) {
            StyleInformation styleInformation = optionalStyleInformation.get();
            styleInformation.setWebsiteName(styleInformationDetails.getWebsiteName());
            styleInformation.setWebsiteLogo(styleInformationDetails.getWebsiteLogo());
            styleInformation.setUiThemes(styleInformationDetails.getUiThemes());

            StyleInformation updatedStyleInformation = styleInformationRepository.save(styleInformation);
            return ResponseEntity.ok(updatedStyleInformation);
        } else {
            throw new EmployeeNotFoundException(styleId);
        }
    }

    @PostMapping("/uploadLogo/{styleId}")
    public ResponseEntity<StyleInformation> uploadLogo(
            @PathVariable Long styleId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Optional<StyleInformation> optionalStyleInformation = styleInformationRepository.findById(styleId);
        if (optionalStyleInformation.isEmpty()) {
            throw new EmployeeNotFoundException(styleId);
        }

        StyleInformation styleInformation = optionalStyleInformation.get();

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        String filePath = uploadDir + fileName;
        File dest = new File(filePath);
        file.transferTo(dest);

        styleInformation.setWebsiteLogo("/public/uploads/" + fileName);
        StyleInformation updatedStyleInformation = styleInformationRepository.save(styleInformation);

        return ResponseEntity.ok(updatedStyleInformation);
    }
}
