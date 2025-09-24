package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.request.CreateLookupCommandRequest;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.infrastructure.config.cache.CacheNames;
import toolyverse.io.toolyverse.infrastructure.config.cache.RedisCacheConfig;
import toolyverse.io.toolyverse.infrastructure.handler.CommandWithParam;

@Service
@RequiredArgsConstructor
public class CreateLookupCommandHandler implements CommandWithParam<CreateLookupCommandRequest> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @CacheEvict(value = CacheNames.CACHE_ALL_LOOKUPS, allEntries = true, cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
    @Override
    @Transactional
    public void execute(CreateLookupCommandRequest request) {
        // Resolve parent's ID from its code.
        String parentCode = resolveParentCode(request.getParentCode());

        // Validate that the new lookup code is unique in its context.
        validateUniqueness(request.getCode(), parentCode);

        // If validations pass, create and save the new lookup.
        var lookup = lookupMapper.toEntity(request);
        lookup.setParentCode(parentCode);
        lookupRepository.save(lookup);
    }

    // --- Private Helper Methods ---

    private String resolveParentCode(String parentCode) {
        if (!StringUtils.hasText(parentCode)) {
            return null;
        }

        return lookupRepository.findByCode(parentCode)
                .map(Lookup::getCode)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Parent lookup with code '%s' not found.", parentCode)
                ));
    }

    private void validateUniqueness(String code, String parentCode) {
        if (parentCode != null) {
            // Check for uniqueness within the parent group.
            if (lookupRepository.existsByParentCodeAndCode(parentCode, code)) {
                throw new IllegalStateException(
                        String.format("A lookup item with code '%s' already exists in the group with code '%s'.",
                                code, parentCode)
                );
            }
        } else {
            // Check for uniqueness among top-level groups.
            if (lookupRepository.existsByParentCodeIsNullAndCode(code)) {
                throw new IllegalStateException(
                        String.format("A top-level lookup group with code '%s' already exists.", code)
                );
            }
        }
    }

}