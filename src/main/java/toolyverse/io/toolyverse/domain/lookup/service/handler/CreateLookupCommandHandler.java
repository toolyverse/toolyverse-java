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
        // Step 1: Check if a lookup with the same code already exists under the same parent
        if (lookupRepository.existsByParentIdAndCode(request.getParentId(), request.getCode())) {
            throw new IllegalStateException(
                    String.format("A lookup with code '%s' already exists in the group with id '%d'.",
                            request.getCode(), request.getParentId())
            );
        }

        // Step 2: If no duplicate is found, proceed with creation
        var lookup = lookupMapper.toEntity(request);
        lookupRepository.save(lookup);
    }
}