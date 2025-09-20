package toolyverse.io.toolyverse.domain.lookup.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import toolyverse.io.toolyverse.domain.lookup.enumeration.LookupType;
import toolyverse.io.toolyverse.domain.shared.model.request.BaseFilterRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class LookupFilterRequest extends BaseFilterRequest {
    @Schema(description = "Filter by lookup code (case-insensitive partial match).", example = "STATUS")
    private String code;

    @Schema(description = "Filter by a specific lookup type.", example = "GROUP")
    private LookupType lookupType;

    @Schema(description = "Filter by active status.", example = "true")
    private Boolean isActive;
}
