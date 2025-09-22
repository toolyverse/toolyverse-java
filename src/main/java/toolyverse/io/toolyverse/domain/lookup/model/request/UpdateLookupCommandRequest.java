package toolyverse.io.toolyverse.domain.lookup.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request Object for updating a lookup item.")
public class UpdateLookupCommandRequest {

    @Schema(description = "Unique code for the lookup item (used for identification only, not updated).",
            example = "APPROVED", accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    @Schema(description = "Detailed description of the lookup.", example = "Status for items that have been approved.")
    private String description;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    @Schema(description = "The code of the parent lookup if this is a child item.", example = "STATUS_CODES")
    private String parentCode;

    @Schema(description = "The active status of the lookup.", example = "true")
    private Boolean isActive;

    @Schema(description = "The display order relative to siblings.", example = "3")
    private Integer displayOrder;

    @Schema(description = "A map of language codes to translated descriptions.", example = "{\"tr\": \"Onaylandı\"}")
    private Map<String, Object> translations;
}
