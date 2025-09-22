package toolyverse.io.toolyverse.domain.lookup.service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;

@Service
@RequiredArgsConstructor
public class DeleteLookupCommandHandler {

    private final LookupRepository lookupRepository;

    @Transactional
    public void execute(String code) {
        Lookup lookupToDelete = lookupRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Lookup not found with code: " + code));

        lookupRepository.delete(lookupToDelete);
    }
}