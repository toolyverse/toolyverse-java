package toolyverse.io.toolyverse.domain.relationships.one2many;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "author", // İlişkinin sahibinin Book entity'sindeki "author" alanı olduğunu belirtir.
            cascade = CascadeType.ALL, // Author üzerindeki işlemler (kaydetme, silme vb.) Book'lara da yansıtılır.
            orphanRemoval = true // Yazarın listesinden bir kitap çıkarılırsa, o kitap veritabanından silinir.
    )
    private Set<Book> books = new HashSet<>();

    // İlişki yönetimi için helper metodlar
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}