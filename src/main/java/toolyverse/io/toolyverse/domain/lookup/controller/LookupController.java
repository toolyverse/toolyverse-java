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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.model.parameter.UpdateLookupUseCaseParam;
import toolyverse.io.toolyverse.domain.lookup.model.request.CreateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.model.request.LookupFilterRequest;
import toolyverse.io.toolyverse.domain.lookup.model.request.UpdateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.service.usecase.*;
import toolyverse.io.toolyverse.infrastructure.response.ApiResponseWrapper;
import toolyverse.io.toolyverse.infrastructure.response.PageableResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
@Tag(name = "Lookup Management", description = "APIs for creating, reading, updating, and deleting system lookups.")
public class LookupController {

    private final CreateLookupUseCase createLookupCommand;
    private final UpdateLookupUseCase updateLookupCommand;
    private final DeleteLookupUseCase deleteLookupCommand;
    private final GetLookupByCodeUseCase getLookupByCodeQuery;
    private final GetAllLookupsUseCase getAllLookupsQuery;
    private final GetLookupsByParentIdUseCase getLookupsByParentIdQuery;
    private final GetAllLookupsWithMapUseCase getAllLookupsWithMapQuery;

    // --- Controller Endpoints ---

    @Operation(summary = "Create a new lookup", description = "Creates a new lookup item or group.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lookup created successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<Object>> createLookup(@Valid @RequestBody CreateLookupCommandRequest request) {
        createLookupCommand.execute(request);
        return new ResponseEntity<>(ApiResponseWrapper.successWithEmptyData(), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing lookup", description = "Updates the details of an existing lookup by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup updated successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Lookup not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponseWrapper<Object>> updateLookup(
            @Parameter(description = "Code of the lookup to update.", required = true, example = "PENDING_APPROVAL") @PathVariable String code,
            @Valid @RequestBody UpdateLookupCommandRequest request) {
        updateLookupCommand.execute(new UpdateLookupUseCaseParam(code, request));
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
        deleteLookupCommand.execute(id);
        return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
    }

    @Operation(summary = "Get a lookup by ID", description = "Retrieves the details of a specific lookup by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lookup found.", content = @Content(schema = @Schema(implementation = LookupResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lookup not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
    })
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponseWrapper<LookupDto>> getLookupByCode(
            @Parameter(description = "Code of the lookup to retrieve.", required = true, example = "PENDING_APPROVAL") @PathVariable String code) {
        LookupDto lookup = getLookupByCodeQuery.execute(code);
        return ResponseEntity.ok(ApiResponseWrapper.success(lookup));
    }

    @Operation(summary = "Get all lookups with filtering and pagination", description = "Retrieves a paginated list of lookups based on filter criteria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lookups.", content = @Content(schema = @Schema(implementation = LookupPageResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<PageableResponse<LookupDto>>> getAllLookups(
            @ParameterObject @Valid LookupFilterRequest filter) {
        Page<LookupDto> lookupsPage = getAllLookupsQuery.execute(filter);
        return ResponseEntity.ok(ApiResponseWrapper.success(lookupsPage));
    }


    @Operation(summary = "Get lookups by parent ID", description = "Retrieves a list of all child lookups for a given parent ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of child lookups.", content = @Content(schema = @Schema(implementation = LookupListResponse.class)))
    })
    @GetMapping("/by-parent/{parentCode}")
    public ResponseEntity<ApiResponseWrapper<List<LookupDto>>> getLookupsByParent(
            @Parameter(description = "ID of the parent lookup.", required = true, example = "STATUS") @PathVariable String parentCode) {
        List<LookupDto> lookups = getLookupsByParentIdQuery.execute(parentCode);
        return ResponseEntity.ok(ApiResponseWrapper.success(lookups));
    }


    @Operation(summary = "Get all lookups", description = "Retrieves all list of lookups.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lookups.", content = @Content(schema = @Schema(implementation = LookupAllResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<Map<String, List<LookupDto>>>> getAllLookupsWithoutPagination() {
        var lookupsPage = getAllLookupsWithMapQuery.execute();
        return ResponseEntity.ok(ApiResponseWrapper.success(lookupsPage));
    }

    // --- OpenAPI Schema Helper Classes ---


    @Schema(name = "LookupResponse", description = "API response containing a single lookup object.")
    public static class LookupResponse extends ApiResponseWrapper<LookupDto> {
    }

    @Schema(name = "LookupListResponse", description = "API response containing a list of lookup objects.")
    public static class LookupListResponse extends ApiResponseWrapper<List<LookupDto>> {
    }

    @Schema(name = "LookupPageResponse", description = "API response containing a paginated list of lookup objects.")
    public static class LookupPageResponse extends ApiResponseWrapper<PageableResponse<LookupDto>> {
    }

    @Schema(name = "LookupAllResponse", description = "API response containing list of lookup objects.")
    public static class LookupAllResponse extends ApiResponseWrapper<Map<String, List<LookupDto>>> {
    }
}