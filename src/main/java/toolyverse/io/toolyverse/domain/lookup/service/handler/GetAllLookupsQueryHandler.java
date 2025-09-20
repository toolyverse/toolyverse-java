package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllLookupsQueryHandler {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Transactional(readOnly = true)
    public List<LookupDto> execute() {
        var lookups = lookupRepository.findAll();
        return lookupMapper.toDtoList(lookups);
    }
}
