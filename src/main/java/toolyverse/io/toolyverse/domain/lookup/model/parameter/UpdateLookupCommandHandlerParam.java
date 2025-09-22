package toolyverse.io.toolyverse.domain.lookup.model.parameter;

import toolyverse.io.toolyverse.domain.lookup.model.request.UpdateLookupCommandRequest;

public record UpdateLookupCommandHandlerParam(
        String code,
        UpdateLookupCommandRequest request
) {
}