package toolyverse.io.toolyverse.infrastructure.config.hibernate;

import jakarta.persistence.PreRemove;
import org.springframework.data.domain.AuditorAware;
import toolyverse.io.toolyverse.domain.shared.entity.BaseEntity;
import toolyverse.io.toolyverse.infrastructure.config.BeanUtil;

import java.time.LocalDateTime;

public class SoftDeleteListener {

    @PreRemove
    public void preRemove(BaseEntity entity) {
        // Set the deleted timestamp
        entity.setDeletedAt(LocalDateTime.now());

        // Get the AuditorAware bean from the Spring context
        @SuppressWarnings("unchecked") // Safe to suppress as we know the bean's type from JpaAuditingConfig
        AuditorAware<String> auditorAware = (AuditorAware<String>) BeanUtil.getBean(AuditorAware.class);

        // Set the 'deletedBy' field with the current user's name
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }
}