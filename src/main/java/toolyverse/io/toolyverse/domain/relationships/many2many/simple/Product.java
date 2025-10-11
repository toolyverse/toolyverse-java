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
@ToString(exclude = "categories") // Sonsuz döngüyü engelle
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_category", // Oluşturulacak ara tablonun adı
            joinColumns = @JoinColumn(name = "product_id"), // Bu entity'nin (Product) ara tablodaki kolon adı
            inverseJoinColumns = @JoinColumn(name = "category_id") // Karşı entity'nin (Category) ara tablodaki kolon adı
    )
    private Set<Category> categories = new HashSet<>();

    // --- İlişkiyi senkronize tutmak için yardımcı metotlar ---

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getProducts().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getProducts().remove(this);
    }
}