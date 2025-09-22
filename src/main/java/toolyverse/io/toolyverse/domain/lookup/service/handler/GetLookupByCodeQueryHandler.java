package toolyverse.io.toolyverse.domain.lookup.service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.infrastructure.handler.QueryWithParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLookupByCodeQueryHandler implements QueryWithParam<String, LookupDto> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Override
    public LookupDto execute(String code) {
        log.info("Get Lookup by code: {}", code);
        return lookupRepository.findByCode(code)
                .map(lookupMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Lookup not found with code: " + code));
    }
}