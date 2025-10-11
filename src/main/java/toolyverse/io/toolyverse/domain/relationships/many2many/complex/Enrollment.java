package toolyverse.io.toolyverse.domain.relationships.many2many.complex;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(EnrollmentId.class)
public class Enrollment {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "grade")
    private String grade; // Notu tutacak alan (Örn: "AA", "BB" veya 85.5)

}