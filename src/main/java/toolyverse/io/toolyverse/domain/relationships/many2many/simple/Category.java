package toolyverse.io.toolyverse.domain.relationships.many2many.simple;

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
@ToString(exclude = "products") // Sonsuz döngüyü engelle
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "categories") // İlişkinin Product'taki "categories" alanı tarafından yönetildiğini belirtir.
    private Set<Product> products = new HashSet<>();

}