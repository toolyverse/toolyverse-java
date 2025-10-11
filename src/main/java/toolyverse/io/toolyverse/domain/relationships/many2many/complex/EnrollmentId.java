package toolyverse.io.toolyverse.domain.relationships.many2many.complex;


import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class EnrollmentId implements Serializable {
    private Long student;
    private Long course;
}