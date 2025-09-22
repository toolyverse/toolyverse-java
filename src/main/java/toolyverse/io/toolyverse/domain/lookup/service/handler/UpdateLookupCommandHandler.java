package toolyverse.io.toolyverse.domain.lookup.service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.parameter.UpdateLookupCommandHandlerParam;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.infrastructure.handler.CommandWithParam;


@Service
@RequiredArgsConstructor
public class UpdateLookupCommandHandler implements CommandWithParam<UpdateLookupCommandHandlerParam> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Transactional
    public void execute(UpdateLookupCommandHandlerParam param) {
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