package toolyverse.io.toolyverse.domain.lookup.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import toolyverse.io.toolyverse.domain.lookup.enumeration.LookupType;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request Object for creating Lookup entity.")
public class CreateLookupCommandRequest {
    @NotBlank(message = "Code cannot be blank.")
    @Size(min = 2, max = 100, message = "Code must be between 2 and 100 characters.")
    @Schema(description = "Unique code for the new lookup item.", example = "PENDING_APPROVAL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    @Schema(description = "Detailed description for the new lookup.", example = "Status for items awaiting administrator approval.")
    private String description;

    @NotNull(message = "Lookup type must be specified.")
    @Schema(description = "The type of the lookup, either GROUP or ITEM.", example = "ITEM", requiredMode = Schema.RequiredMode.REQUIRED)
    private LookupType lookupType;

    @Schema(description = "The ID of the parent lookup if this is a child item.", example = "50")
    private Long parentId;

    @Schema(description = "Set the active status. Defaults to true if not provided.", example = "true")
    private Boolean isActive = true;

    @Schema(description = "The display order relative to siblings.", example = "2")
    private Integer displayOrder;

    @Schema(description = "A map of language codes to translated descriptions.", example = "{\"tr\": \"Onay Bekliyor\"}")
    private Map<String, Object> translations;
}
