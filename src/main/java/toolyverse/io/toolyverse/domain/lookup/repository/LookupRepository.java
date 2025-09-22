package toolyverse.io.toolyverse.domain.lookup.repository;

import org.springframework.stereotype.Repository;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.shared.repository.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LookupRepository extends BaseJpaRepository<Lookup, Long> {

    Optional<Lookup> findByCode(String code);

    List<Lookup> findByParentId(Long parentId);

    boolean existsByParentIdAndCode(Long parentId, String code);

    boolean existsByParentIdIsNullAndCode(String code);
}