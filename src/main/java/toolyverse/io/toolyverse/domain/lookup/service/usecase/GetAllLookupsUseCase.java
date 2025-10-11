package toolyverse.io.toolyverse.domain.lookup.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.model.request.LookupFilterRequest;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.domain.lookup.repository.spesification.LookupSpecification;
import toolyverse.io.toolyverse.domain.shared.repository.specification.BaseSpecification;
import toolyverse.io.toolyverse.infrastructure.usecase.UseCase;


@Service
@RequiredArgsConstructor
public class GetAllLookupsUseCase implements UseCase<LookupFilterRequest, Page<LookupDto>> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Override
    public Page<LookupDto> execute(LookupFilterRequest filter) {

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDir(), filter.getSortBy())
        );

        // Start with a neutral specification
        Specification<Lookup> spec = (_, _, _) -> null;
        spec = spec
                .and(LookupSpecification.hasCodeLike(filter.getCode()))
                .and(LookupSpecification.isActive(filter.getIsActive()))
                .and(LookupSpecification.isType(filter.getLookupType()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));

        Page<Lookup> lookupsPage = lookupRepository.findAll(spec, pageable);
        return lookupsPage.map(lookupMapper::toDto);
    }
}