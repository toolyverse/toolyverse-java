package toolyverse.io.toolyverse.domain.lookup.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.domain.shared.enumeration.DeletedStatus;
import toolyverse.io.toolyverse.infrastructure.config.cache.CacheNames;
import toolyverse.io.toolyverse.infrastructure.config.cache.RedisCacheConfig;
import toolyverse.io.toolyverse.infrastructure.usecase.UseCaseWithInput;

@Service
@RequiredArgsConstructor
public class DeleteLookupUseCase implements UseCaseWithInput<Long> {

    private final LookupRepository lookupRepository;

    @CacheEvict(value = CacheNames.CACHE_ALL_LOOKUPS, allEntries = true, cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
    @Override
    @Transactional
    public void execute(Long id) {
        Lookup lookupToDelete = lookupRepository.findById(id, DeletedStatus.DELETED_FALSE)
                .orElseThrow(() -> new RuntimeException("Lookup not found with id: " + id));

        lookupRepository.delete(lookupToDelete);
    }
}