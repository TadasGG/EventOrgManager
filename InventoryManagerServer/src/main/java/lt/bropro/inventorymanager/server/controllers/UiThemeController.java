package lt.bropro.inventorymanager.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.bropro.inventorymanager.server.database.StyleInformationRepository;
import lt.bropro.inventorymanager.server.database.UiThemeRepository;
import lt.bropro.inventorymanager.server.exceptions.EmployeeNotFoundException;
import lt.bropro.inventorymanager.server.exceptions.RoleNotFoundException;
import lt.bropro.inventorymanager.server.model.Role;
import lt.bropro.inventorymanager.server.model.StyleInformation;
import lt.bropro.inventorymanager.server.model.UiThemes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public/styles/themes")
@Tag(name = "Web Style Theme Controller")
public class UiThemeController {
    @Autowired
    private UiThemeRepository uiThemeRepository;

    @Operation(
            summary = "Get all UI themes.",
            description = "Returns all themes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all themes.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UiThemes.class))}),
            @ApiResponse(responseCode = "404", description = "Themes not found.",
                    content = @Content)
    })
    @GetMapping
    public List<UiThemes> getAllThemes() {
        return uiThemeRepository.findAll();
    }

    @Operation(
            summary = "Get a theme by ID",
            description = "Retrieve a theme by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the theme",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UiThemes.class))}),
            @ApiResponse(responseCode = "404", description = "Theme not found",
                    content = @Content)
    })
    @GetMapping("/{themeId}")
    public ResponseEntity<UiThemes> getThemeById(@PathVariable Long themeId) {
        Optional<UiThemes> optionalUiThemes = uiThemeRepository.findById(themeId);
        if (optionalUiThemes.isPresent()) {
            UiThemes uiThemes = optionalUiThemes.get();
            return ResponseEntity.ok().body(uiThemes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
