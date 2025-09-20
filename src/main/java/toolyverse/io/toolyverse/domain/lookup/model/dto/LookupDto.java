package toolyverse.io.toolyverse.domain.lookup.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import toolyverse.io.toolyverse.domain.lookup.enumeration.LookupType;

import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object representing a Lookup entity.")
public class LookupDto {
    @Schema(description = "The unique identifier of the lookup entity.", example = "1")
    private Long id;

    @Schema(description = "The unique code for the lookup item.", example = "ACTIVE_STATUS")
    private String code;

    @Schema(description = "A detailed description of what the lookup represents.", example = "Indicates that an item is currently active and operational.")
    private String description;

    @Schema(description = "The type of the lookup, which can be a GROUP or an ITEM.", example = "ITEM")
    private LookupType lookupType;

    @Schema(description = "The ID of the parent lookup if this is a hierarchical item. Null for root items.", example = "100")
    private Long parentId;

    @Schema(description = "Flag to indicate if the lookup is active and available for use.", example = "true")
    private Boolean isActive;

    @Schema(description = "The order in which to display this item relative to its siblings.", example = "1")
    private Integer displayOrder;

    @Schema(description = "A map of language codes to translated descriptions.", example = "{\"tr\": \"Aktif Durum\", \"de\": \"Activer Status\"}")
    private Map<String, Object> translations;
}


