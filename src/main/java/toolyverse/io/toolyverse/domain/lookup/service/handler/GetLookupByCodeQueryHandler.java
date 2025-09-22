package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

import jakarta.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLookupByCodeQueryHandler {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Transactional(readOnly = true)
    public LookupDto execute(String code) {
        log.info("Get Lookup by code: {}", code);
        return lookupRepository.findByCode(code)
                .map(lookupMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Lookup not found with code: " + code));
    }
}