package toolyverse.io.toolyverse.domain.relationships.many2many.simple;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "category") // Sonsuz döngüyü engellemek için
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // Bu kolon, product tablosunda foreign key olacaktır.
    private Category category;

}