package toolyverse.io.toolyverse.domain.lookup.service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

@Service
@RequiredArgsConstructor
public class DeleteLookupCommandHandler {

    private final LookupRepository lookupRepository;

    @Transactional
    public void execute(Long id) {
        if (!lookupRepository.existsById(id)) {
            throw new EntityNotFoundException("Lookup not found with id: " + id);
        }
        lookupRepository.deleteById(id);
    }
}
