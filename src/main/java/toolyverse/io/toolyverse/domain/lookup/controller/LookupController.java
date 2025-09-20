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
import toolyverse.io.toolyverse.infrastructure.response.ApiResponseWrapper;

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

    // --- Controller Endpoints ---

    @Operation(summary = "Create a new lookup", description = "Creates a new lookup item or group.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lookup created successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<Object>> createLookup(@Valid @RequestBody CreateLookupCommandRequest request) {
        createLookupCommandHandler.execute(request);
        return new ResponseEntity<>(ApiResponseWrapper.successWithEmptyData(), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing lookup", description = "Updates the details of an existing lookup by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup updated successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Lookup not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Object>> updateLookup(
            @Parameter(description = "ID of the lookup to update.", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateLookupCommandRequest request) {
        updateLookupCommandHandler.execute(id, request);
        return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
    }

    @Operation(summary = "Delete a lookup", description = "Deletes a lookup by its ID. This performs a soft delete.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup deleted successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Lookup not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Object>> deleteLookup(
            @Parameter(description = "ID of the lookup to delete.", required = true, example = "1") @PathVariable Long id) {
        deleteLookupCommandHandler.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
    }

    @Operation(summary = "Get a lookup by ID", description = "Retrieves the details of a specific lookup by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup found.", content = @Content(schema = @Schema(implementation = LookupResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lookup not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<LookupDto>> getLookupById(
            @Parameter(description = "ID of the lookup to retrieve.", required = true, example = "1") @PathVariable Long id) {
        LookupDto lookup = getLookupByIdQueryHandler.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(lookup));
    }

    @Operation(summary = "Get all lookups", description = "Retrieves a list of all lookups in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lookups.", content = @Content(schema = @Schema(implementation = LookupListResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<LookupDto>>> getAllLookups() {
        List<LookupDto> lookups = getAllLookupsQueryHandler.execute();
        return ResponseEntity.ok(ApiResponseWrapper.success(lookups));
    }

    @Operation(summary = "Get lookups by parent ID", description = "Retrieves a list of all child lookups for a given parent ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of child lookups.", content = @Content(schema = @Schema(implementation = LookupListResponse.class)))
    })
    @GetMapping("/by-parent/{parentId}")
    public ResponseEntity<ApiResponseWrapper<List<LookupDto>>> getLookupsByParent(
            @Parameter(description = "ID of the parent lookup.", required = true, example = "10") @PathVariable Long parentId) {
        List<LookupDto> lookups = getLookupsByParentIdQueryHandler.execute(parentId);
        return ResponseEntity.ok(ApiResponseWrapper.success(lookups));
    }


    // --- OpenAPI Schema Helper Classes ---

    /**
     * OpenAPI-specific schema for a response containing a single LookupDto.
     * This class is defined here solely for documentation purposes.
     */
    @Schema(name = "LookupResponse", description = "API response containing a single lookup object.")
    public static class LookupResponse extends ApiResponseWrapper<LookupDto> {
    }

    /**
     * OpenAPI-specific schema for a response containing a list of LookupDto objects.
     * This class is defined here solely for documentation purposes.
     */
    @Schema(name = "LookupListResponse", description = "API response containing a list of lookup objects.")
    public static class LookupListResponse extends ApiResponseWrapper<List<LookupDto>> {
    }
}