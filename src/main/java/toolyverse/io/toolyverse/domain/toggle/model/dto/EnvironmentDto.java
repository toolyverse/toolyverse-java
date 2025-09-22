package toolyverse.io.toolyverse.domain.toggle.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public  class EnvironmentDto {
    private Long id;
    private String code;
    private String description;
    private Boolean isActive;
    private Integer displayOrder;

    // Constructor for JPA query projection
    public EnvironmentDto(Long id, String code, String description, Boolean isActive, Integer displayOrder) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
    }
}
