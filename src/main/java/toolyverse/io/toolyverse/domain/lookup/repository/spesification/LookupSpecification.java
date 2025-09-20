package toolyverse.io.toolyverse.domain.lookup.repository.spesification;

import org.springframework.data.jpa.domain.Specification;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.enumeration.LookupType;
import toolyverse.io.toolyverse.domain.shared.repository.specification.BaseSpecification;

public class LookupSpecification {

    public static Specification<Lookup> hasCodeLike(String code) {
        return BaseSpecification.like("code", code);
    }

    public static Specification<Lookup> hasLookupType(LookupType lookupType) {
        return BaseSpecification.equals("lookupType", lookupType);
    }

    public static Specification<Lookup> isActive(Boolean isActive) {
        return BaseSpecification.equals("isActive", isActive);
    }
}