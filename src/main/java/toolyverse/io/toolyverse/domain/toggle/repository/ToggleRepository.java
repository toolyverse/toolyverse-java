package toolyverse.io.toolyverse.domain.toggle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import toolyverse.io.toolyverse.domain.shared.repository.BaseJpaRepository;
import toolyverse.io.toolyverse.domain.toggle.entity.Toggle;
import toolyverse.io.toolyverse.domain.toggle.model.dto.ToggleWithEnvironmentsDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToggleRepository extends BaseJpaRepository<Toggle, Long> {


    /**
     * Find toggle by key
     */
    Optional<Toggle> findByToggleKey(String toggleKey);

    /**
     * Find toggle by key and environment code
     */
    @Query("SELECT t FROM Toggle t JOIN t.environments e WHERE t.toggleKey = :toggleKey AND e.code = :environmentCode")
    Optional<Toggle> findByToggleKeyAndEnvironmentCode(@Param("toggleKey") String toggleKey,
                                                       @Param("environmentCode") String environmentCode);

    /**
     * Find all toggles for a specific environment with pagination
     */
    @Query("SELECT DISTINCT t FROM Toggle t JOIN t.environments e WHERE e.code = :environmentId")
    Page<Toggle> findAllByEnvironmentCode(@Param("environmentCode") String environmentCode, Pageable pageable);


    /**
     * Check if a toggle exists and is enabled in a specific environment by code
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Toggle t JOIN t.environments e " +
            "WHERE t.toggleKey = :toggleKey AND e.code = :environmentCode")
    boolean isToggleExistsByEnvironmentCode(@Param("toggleKey") String toggleKey, @Param("environmentCode") String environmentCode);

    /**
     * Find toggles with multiple environments
     */
    @Query("SELECT new toolyverse.io.toolyverse.domain.toggle.model.dto.ToggleWithEnvironmentsDto(" +
            "t.id, t.toggleKey, t.isEnabled, t.description, t.createdAt, t.updatedAt) " +
            "FROM Toggle t")
    Page<ToggleWithEnvironmentsDto> findAllTogglesWithEnvironments(Pageable pageable);

}
