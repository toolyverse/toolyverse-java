package toolyverse.io.toolyverse.domain.relationships.one2one;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private IDCard idCard;

    public void setIdCard(IDCard idCard) {
        if (idCard == null) {
            if (this.idCard != null) {
                this.idCard.setPerson(null);
            }
        } else {
            idCard.setPerson(this);
        }
        this.idCard = idCard;
    }
}