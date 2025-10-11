package toolyverse.io.toolyverse.domain.relationships.many2many.complex;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "enrollments")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(
            mappedBy = "course", // İlişki Enrollment'taki "course" alanı tarafından yönetiliyor.
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();
}