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
@ToString(exclude = "products") // Sonsuz döngüyü engellemek için
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.PERSIST // Kategori kaydedilince ürünler de edilsin
    )
    private Set<Product> products = new HashSet<>();

    // --- İlişkiyi senkronize tutmak için yardımcı metotlar ---

    public void addProduct(Product product) {
        this.products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        product.setCategory(null);
    }
}