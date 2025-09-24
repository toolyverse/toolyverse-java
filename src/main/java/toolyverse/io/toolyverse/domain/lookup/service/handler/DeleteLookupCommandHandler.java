package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.domain.shared.enumeration.DeletedStatus;
import toolyverse.io.toolyverse.infrastructure.config.cache.CacheNames;
import toolyverse.io.toolyverse.infrastructure.config.cache.RedisCacheConfig;
import toolyverse.io.toolyverse.infrastructure.handler.CommandWithParam;

@Service
@RequiredArgsConstructor
public class DeleteLookupCommandHandler implements CommandWithParam<Long> {

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