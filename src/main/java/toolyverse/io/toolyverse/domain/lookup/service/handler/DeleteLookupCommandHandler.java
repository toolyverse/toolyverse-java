package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.domain.shared.enumeration.DeletedStatus;

@Service
@RequiredArgsConstructor
public class DeleteLookupCommandHandler {

    private final LookupRepository lookupRepository;

    @Transactional
    public void execute(Long id) {
        Lookup lookupToDelete = lookupRepository.findById(id, DeletedStatus.DELETED_FALSE)
                .orElseThrow(() -> new RuntimeException("Lookup not found with id: " + id));

        lookupRepository.delete(lookupToDelete);
    }
}