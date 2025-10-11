package toolyverse.io.toolyverse.domain.lookup.service.usecase;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.parameter.UpdateLookupUseCaseParam;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.infrastructure.config.cache.CacheNames;
import toolyverse.io.toolyverse.infrastructure.config.cache.RedisCacheConfig;
import toolyverse.io.toolyverse.infrastructure.usecase.UseCaseWithInput;


@Service
@RequiredArgsConstructor
public class UpdateLookupUseCase implements UseCaseWithInput<UpdateLookupUseCaseParam> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @CacheEvict(value = CacheNames.CACHE_ALL_LOOKUPS, allEntries = true, cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_FIVE_MINUTES)
    @Override
    @Transactional
    public void execute(UpdateLookupUseCaseParam param) {
        var code = param.code();
        var request = param.request();
        Lookup lookup = findLookupByCode(code, request.getParentCode());

        // Only update allowed fields (excluding code and parentCode)
        lookupMapper.updateEntityFromRequest(request, lookup);
        lookupRepository.save(lookup);
    }

    private Lookup findLookupByCode(String code, String parentCode) {
        var lookupOptional = lookupRepository.findByParentCodeAndCode(code, parentCode);

        if (lookupOptional.isPresent()) {
            return lookupOptional.get();
        }

        // If not found as direct item, try to find as parent
        var parentLookupOptional = lookupRepository.findByCodeAndParentCodeIsNull(code);

        if (parentLookupOptional.isPresent()) {
            return parentLookupOptional.get();
        }

        throw new EntityNotFoundException("Lookup not found with code: " + code + " (searched as both item and parent)");
    }
}