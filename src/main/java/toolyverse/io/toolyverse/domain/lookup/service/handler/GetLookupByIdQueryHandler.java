package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

import static toolyverse.io.toolyverse.infrastructure.exception.ExceptionMessage.NOT_FOUND_EXCEPTION;
import static toolyverse.io.toolyverse.infrastructure.exception.ExceptionUtil.buildException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLookupByIdQueryHandler {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Transactional(readOnly = true)
    public LookupDto execute(Long id) {
        log.info("Get Lookup by id: {}", id);
        return lookupRepository.findById(id)
                .map(lookupMapper::toDto)
                .orElseThrow(() -> buildException(NOT_FOUND_EXCEPTION, id));
    }
}
