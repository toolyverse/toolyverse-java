package toolyverse.io.toolyverse.domain.lookup.repository;

import org.springframework.stereotype.Repository;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.shared.repository.BaseJpaRepository;

@Repository
public interface LookupRepository extends BaseJpaRepository<Lookup, Long> {
}
