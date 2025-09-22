package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
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
        // Resolve parent's ID from its code.
        Long parentId = resolveParentId(request.getParentCode());

        // Validate that the new lookup code is unique in its context.
        validateUniqueness(request.getCode(), parentId, request.getParentCode());

        // If validations pass, create and save the new lookup.
        var lookup = lookupMapper.toEntity(request);
        lookup.setParentId(parentId);
        lookupRepository.save(lookup);
    }

    // --- Private Helper Methods ---

    private Long resolveParentId(String parentCode) {
        if (!StringUtils.hasText(parentCode)) {
            return null;
        }

        return lookupRepository.findByCode(parentCode)
                .map(Lookup::getId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Parent lookup with code '%s' not found.", parentCode)
                ));
    }

    private void validateUniqueness(String code, Long parentId, String parentCode) {
        if (parentId != null) {
            // Check for uniqueness within the parent group.
            if (lookupRepository.existsByParentIdAndCode(parentId, code)) {
                throw new IllegalStateException(
                        String.format("A lookup item with code '%s' already exists in the group with code '%s'.",
                                code, parentCode)
                );
            }
        } else {
            // Check for uniqueness among top-level groups.
            if (lookupRepository.existsByParentIdIsNullAndCode(code)) {
                throw new IllegalStateException(
                        String.format("A top-level lookup group with code '%s' already exists.", code)
                );
            }
        }
    }
}