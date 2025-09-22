package toolyverse.io.toolyverse.domain.lookup.mapper;

import org.mapstruct.*;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.model.request.CreateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.model.request.UpdateLookupCommandRequest;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LookupMapper {

    Lookup toEntity(CreateLookupCommandRequest request);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "parentCode", ignore = true)
    void updateEntityFromRequest(UpdateLookupCommandRequest request, @MappingTarget Lookup entity);

    LookupDto toDto(Lookup entity);

    List<LookupDto> toDtoList(List<Lookup> entities);
}