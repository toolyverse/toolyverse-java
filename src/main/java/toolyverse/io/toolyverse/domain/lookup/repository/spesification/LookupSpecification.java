package toolyverse.io.toolyverse.domain.lookup.repository.spesification;

import org.springframework.data.jpa.domain.Specification;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.lookup.enumeration.LookupType;
import toolyverse.io.toolyverse.domain.shared.repository.specification.BaseSpecification;

public class LookupSpecification {

    public static Specification<Lookup> hasCodeLike(String code) {
        return BaseSpecification.like("code", code);
    }

    public static Specification<Lookup> isActive(Boolean isActive) {
        return BaseSpecification.equals("isActive", isActive);
    }

    public static Specification<Lookup> isType(LookupType lookupType) {
        return (root, query, cb) -> {
            if (lookupType == null) {
                return null;
            }
            if (lookupType == LookupType.GROUP) {
                return cb.isNull(root.get("parentId"));
            } else { // Assumes ITEM
                return cb.isNotNull(root.get("parentId"));
            }
        };
    }
}