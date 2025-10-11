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
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "student", // İlişki Enrollment'taki "student" alanı tarafından yönetiliyor.
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();

    // --- Yardımcı Metot ---
    // Bir öğrenciyi bir derse notuyla birlikte kaydetmek için kullanılır.
    public void addCourse(Course course, String grade) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(this);
        enrollment.setCourse(course);
        enrollment.setGrade(grade);

        this.enrollments.add(enrollment);
        course.getEnrollments().add(enrollment);
    }
}