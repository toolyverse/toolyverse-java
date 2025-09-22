package toolyverse.io.toolyverse.domain.lookup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.proxy.HibernateProxy;
import toolyverse.io.toolyverse.domain.lookup.enumeration.LookupType;
import toolyverse.io.toolyverse.domain.shared.entity.BaseEntity;
import toolyverse.io.toolyverse.infrastructure.util.JsonConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
// Add the unique constraint here
@Table(name = "lookups",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lookup_parent_code", columnNames = {"parent_id", "code"})
        },
        indexes = {
                @Index(name = "idx_lookup_type", columnList = "lookup_type"),
                @Index(name = "idx_lookup_active", columnList = "is_active"),
                @Index(name = "idx_lookup_hierarchy", columnList = "parent_id, display_order")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE lookups SET deleted_at = now() WHERE id = ?")
public class Lookup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "lookup_type", nullable = false, length = 10)
    private LookupType lookupType;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "translations", columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    @Builder.Default
    private Map<String, Object> translations = new HashMap<>();

    public boolean isGroup() {
        return LookupType.GROUP.equals(this.lookupType);
    }

    public boolean isItem() {
        return LookupType.ITEM.equals(this.lookupType);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Lookup lookup = (Lookup) o;
        return getId() != null && Objects.equals(getId(), lookup.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}