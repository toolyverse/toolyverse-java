package toolyverse.io.toolyverse.domain.shared.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import toolyverse.io.toolyverse.domain.shared.entity.BaseEntity;
import toolyverse.io.toolyverse.domain.shared.enumeration.DeletedStatus;
import toolyverse.io.toolyverse.domain.shared.repository.specification.BaseSpecification;

import java.util.Optional;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deleteReason = :reason WHERE e.id = :id")
    void softDelete(@Param("id") ID id, @Param("reason") String reason);

    default Optional<T> findById(ID id, DeletedStatus deletedStatus) {
        Specification<T> spec = BaseSpecification.<T, ID>byId(id)
                .and(BaseSpecification.deleted(deletedStatus));
        return findOne(spec);
    }

}
