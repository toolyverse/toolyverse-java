package toolyverse.io.toolyverse.domain.lookup.service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.request.UpdateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

@Service
@RequiredArgsConstructor
public class UpdateLookupCommandHandler {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Transactional
    public void execute(Long id, UpdateLookupCommandRequest request) {
        var lookup = lookupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lookup not found with id: " + id));
        lookupMapper.updateEntityFromRequest(request, lookup);
        lookupRepository.save(lookup);
    }
}
