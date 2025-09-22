package toolyverse.io.toolyverse.domain.toggle.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToggleWithEnvironmentsDto {
    private Long id;
    private String toggleKey;
    private Boolean isEnabled;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<EnvironmentDto> environments = new ArrayList<>();

    // Constructor for JPA query projection (without environments)
    public ToggleWithEnvironmentsDto(Long id, String toggleKey, Boolean isEnabled,
                                     String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.toggleKey = toggleKey;
        this.isEnabled = isEnabled;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.environments = new ArrayList<>();
    }


}