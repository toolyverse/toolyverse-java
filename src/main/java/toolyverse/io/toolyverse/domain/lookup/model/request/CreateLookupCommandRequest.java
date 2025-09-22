package toolyverse.io.toolyverse.domain.lookup.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    @Schema(description = "The code of the parent lookup if this is a child item.", example = "STATUS_CODES")
    private String parentCode;

    @Schema(description = "Set the active status. Defaults to true if not provided.", example = "true")
    private Boolean isActive = true;

    @Schema(description = "The display order relative to siblings.", example = "2")
    @Max(value = 1000, message = "Display order must not exceed 1000.")
    @Min(value = 0, message = "Display order must be zero or a positive integer.")
    private Integer displayOrder;

    @Schema(description = "A map of language codes to translated descriptions.", example = "{\"tr\": \"Onay Bekliyor\",\"en\": \"Pending Approval\"}")
    private Map<String, Object> translations;
}
