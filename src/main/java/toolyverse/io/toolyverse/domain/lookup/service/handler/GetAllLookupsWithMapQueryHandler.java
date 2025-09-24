package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.infrastructure.config.cache.CacheNames;
import toolyverse.io.toolyverse.infrastructure.config.cache.RedisCacheConfig;
import toolyverse.io.toolyverse.infrastructure.handler.Query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllLookupsWithMapQueryHandler implements Query<Map<String, List<LookupDto>>> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Cacheable(value = CacheNames.CACHE_ALL_LOOKUPS, cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
    @Override
    public Map<String, List<LookupDto>> execute() {

        List<Lookup> allLookups = lookupRepository.findAllByDeletedAtIsNull();

        return allLookups.stream()
                .collect(Collectors.groupingBy(
                        lookup -> lookup.getParentCode() != null ? lookup.getParentCode() : "ROOT",
                        Collectors.mapping(lookupMapper::toDto, Collectors.toList())
                ));
    }
}