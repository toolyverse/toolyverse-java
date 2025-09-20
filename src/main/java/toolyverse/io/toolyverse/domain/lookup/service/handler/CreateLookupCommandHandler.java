package toolyverse.io.toolyverse.domain.lookup.service.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.request.CreateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

@Service
@RequiredArgsConstructor
public class CreateLookupCommandHandler {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Transactional
    public void execute(CreateLookupCommandRequest request) {
        var lookup = lookupMapper.toEntity(request);
        lookupRepository.save(lookup);
    }
}


