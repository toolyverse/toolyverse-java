package toolyverse.io.toolyverse.domain.toggle.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.proxy.HibernateProxy;
import toolyverse.io.toolyverse.domain.lookup.entity.Lookup;
import toolyverse.io.toolyverse.domain.shared.entity.BaseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "toggles",
        indexes = {
                @Index(name = "idx_toggle_key", columnList = "toggle_key"),
                @Index(name = "idx_toggle_active", columnList = "is_active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE toggles SET deleted_at = now() WHERE id = ?")
public class Toggle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "toggle_key", nullable = false, length = 100)
    private String toggleKey;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean isEnabled = false;

    @Column(name = "description", length = 500)
    private String description;

    // Many-to-Many relationship with Environment (Lookup)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "toggle_environments",
            joinColumns = @JoinColumn(name = "toggle_id"),
            inverseJoinColumns = @JoinColumn(name = "environment_id"),
            indexes = {
                    @Index(name = "idx_toggle_env_toggle_id", columnList = "toggle_id"),
                    @Index(name = "idx_toggle_env_environment_id", columnList = "environment_id")
            }
    )
    @Builder.Default
    private Set<Lookup> environments = new HashSet<>();

    public void addEnvironment(Lookup environment) {
        if (environment != null) {
            this.environments.add(environment);
        }
    }

    public void removeEnvironment(Lookup environment) {
        if (environment != null) {
            this.environments.remove(environment);
        }
    }

    public boolean hasEnvironment(Lookup environment) {
        return environment != null && this.environments.contains(environment);
    }

    public boolean hasEnvironmentById(Long environmentId) {
        return environmentId != null && this.environments.stream()
                .anyMatch(env -> environmentId.equals(env.getId()));
    }

    public void clearEnvironments() {
        this.environments.clear();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", toggleKey='" + toggleKey + '\'' +
                ", isEnabled=" + isEnabled +
                ", environmentCount=" + (environments != null ? environments.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Toggle toggle = (Toggle) o;
        return getId() != null && Objects.equals(getId(), toggle.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }
}