package toolyverse.io.toolyverse.domain.lookup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.model.request.CreateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.model.request.UpdateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.service.handler.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
@Tag(name = "Lookup Management", description = "APIs for creating, reading, updating, and deleting system lookups.")
public class LookupController {

    private final CreateLookupCommandHandler createLookupCommandHandler;
    private final UpdateLookupCommandHandler updateLookupCommandHandler;
    private final DeleteLookupCommandHandler deleteLookupCommandHandler;
    private final GetLookupByIdQueryHandler getLookupByIdQueryHandler;
    private final GetAllLookupsQueryHandler getAllLookupsQueryHandler;
    private final GetLookupsByParentIdQueryHandler getLookupsByParentIdQueryHandler;

    @Operation(summary = "Create a new lookup", description = "Creates a new lookup item or group.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lookup created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request body.")
    })
    @PostMapping
    public ResponseEntity<Void> createLookup(@Valid @RequestBody CreateLookupCommandRequest request) {
        createLookupCommandHandler.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update an existing lookup", description = "Updates the details of an existing lookup by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Lookup not found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLookup(
            @Parameter(description = "ID of the lookup to update.", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateLookupCommandRequest request) {
        updateLookupCommandHandler.execute(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a lookup", description = "Deletes a lookup by its ID. This performs a soft delete.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lookup deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Lookup not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLookup(
            @Parameter(description = "ID of the lookup to delete.", required = true, example = "1") @PathVariable Long id) {
        deleteLookupCommandHandler.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a lookup by ID", description = "Retrieves the details of a specific lookup by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup found.", content = @Content(schema = @Schema(implementation = LookupDto.class))),
            @ApiResponse(responseCode = "404", description = "Lookup not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LookupDto> getLookupById(
            @Parameter(description = "ID of the lookup to retrieve.", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(getLookupByIdQueryHandler.execute(id));
    }

    @Operation(summary = "Get all lookups", description = "Retrieves a list of all lookups in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lookups.")
    })
    @GetMapping
    public ResponseEntity<List<LookupDto>> getAllLookups() {
        return ResponseEntity.ok(getAllLookupsQueryHandler.execute());
    }

    @Operation(summary = "Get lookups by parent ID", description = "Retrieves a list of all child lookups for a given parent ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of child lookups.")
    })
    @GetMapping("/by-parent/{parentId}")
    public ResponseEntity<List<LookupDto>> getLookupsByParent(
            @Parameter(description = "ID of the parent lookup.", required = true, example = "10") @PathVariable Long parentId) {
        return ResponseEntity.ok(getLookupsByParentIdQueryHandler.execute(parentId));
    }
}