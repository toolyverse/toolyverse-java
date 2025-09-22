package toolyverse.io.toolyverse.domain.lookup.repository;

import org.springframework.stereotype.Repository;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.shared.repository.BaseJpaRepository;

import java.util.List;

@Repository
public interface LookupRepository extends BaseJpaRepository<Lookup, Long> {
    List<Lookup> findByParentId(Long parentId);

    boolean existsByParentIdAndCode(Long parentId, String code);
}
