package toolyverse.io.toolyverse.domain.lookup.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toolyverse.io.toolyverse.domain.lookup.mapper.LookupMapper;
import toolyverse.io.toolyverse.domain.lookup.model.dto.LookupDto;
import toolyverse.io.toolyverse.domain.lookup.repository.LookupRepository;
import toolyverse.io.toolyverse.infrastructure.exception.ExceptionMessage;
import toolyverse.io.toolyverse.infrastructure.handler.QueryWithParam;

import java.util.List;

import static toolyverse.io.toolyverse.infrastructure.exception.ExceptionUtil.buildException;

@Service
@RequiredArgsConstructor
public class GetLookupsByParentIdQueryHandler implements QueryWithParam<String, List<LookupDto>> {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    public List<LookupDto> execute(String parentCode) {
        var parentLookup = lookupRepository.findByCodeAndParentCodeIsNullAndDeletedAtIsNull(parentCode)
                .orElseThrow(() -> buildException(ExceptionMessage.NOT_FOUND_EXCEPTION, parentCode));
        var lookups = lookupRepository.findAllByParentCodeAndDeletedAtIsNull(parentLookup.getCode());
        return lookupMapper.toDtoList(lookups);
    }
}
