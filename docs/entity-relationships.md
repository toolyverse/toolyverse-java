# JPA & Hibernate Relationship Mapping Examples

This repository provides a clear and practical demonstration of common entity relationships in JPA and Hibernate, following best practices. Each example is designed to be self-contained and easy to understand.

## Key Concepts & Annotations

Here's a quick reference for the key JPA annotations used to define relationships:

* **`@JoinColumn`**: Placed on the **owning side** of a relationship (usually the `@ManyToOne` or `@OneToOne` side that holds the foreign key). It specifies the foreign key column in the database table. This is used when there is **no join table**.
* **`mappedBy`**: Placed on the **inverse (non-owning) side** of a relationship. It tells Hibernate that the relationship is already being managed by the field specified in `mappedBy = "fieldName"`. This prevents the creation of duplicate foreign keys or join tables.
* **`@JoinTable`**: Used only on the **owning side** of a `@ManyToMany` relationship. It configures the intermediate join table that links the two entities.
    * **`joinColumns`**: Defines the foreign key column in the join table that refers to the **owning entity**.
    * **`inverseJoinColumns`**: Defines the foreign key column in the join table that refers to the **inverse (non-owning) entity**.
* **`cascade`**: Defines how persistence operations (like `save`, `delete`) on a parent entity should **propagate** to its child entities. It's typically placed on the "parent" side (e.g., `@OneToMany`).
* **`orphanRemoval = true`**: Used on `@OneToMany` or `@OneToOne` parent relationships. If a child entity is **removed from the parent's collection**, it will also be automatically deleted from the database. This is useful for managing the lifecycle of tightly coupled entities.

-----

##  Relationship Examples

### 1\. One-to-One: `Person` ←→ `IDCard`

A bidirectional one-to-one relationship where a `Person` has one `IDCard`, and an `IDCard` belongs to one `Person`.

* **Owning Side**: `IDCard`. It contains the `@JoinColumn(name = "person_id")`, which creates the foreign key in the `id_card` table. This side "owns" the relationship.
* **Inverse Side**: `Person`. It uses `mappedBy = "person"` to indicate that the relationship is managed by the `person` field in the `IDCard` class.
* **Lifecycle Management**: `cascade = CascadeType.ALL` and `orphanRemoval = true` are placed on the `Person` (parent) side. This means:
    * Saving a `Person` also saves their associated `IDCard`.
    * Deleting a `Person` also deletes their `IDCard`.
    * Setting `person.setIdCard(null)` will delete the old `IDCard` from the database.
* **Best Practice**: The `Person.setIdCard()` helper method ensures that both sides of the relationship are synchronized in the object model.

#### Code Examples

**`Person.java`**

```java
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
```

**`IDCard.java`**

```java
@Getter
@Setter
@Entity
public class IDCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;
}
```

-----

### 2\. One-to-Many: `Author`  ←→ `Book` 

A bidirectional one-to-many relationship where an `Author` can write many `Book`s, but each `Book` is written by a single `Author`.

* **Owning Side**: `Book` (the "many" side). It contains the `@ManyToOne` annotation and the `@JoinColumn(name = "author_id")`, which creates the foreign key in the `book` table.
* **Inverse Side**: `Author` (the "one" side). It uses `@OneToMany(mappedBy = "author")` to delegate control of the relationship to the `Book` entity.
* **Best Practice**: The `Author` class includes `addBook()` and `removeBook()` helper methods to ensure that when a book is added or removed from an author, the `book.setAuthor(...)` reference is also correctly updated. This prevents data inconsistencies.

#### Code Examples

**`Author.java`**

```java
@Getter
@Setter
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "author",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Book> books = new HashSet<>();

    // Helper methods for relationship management
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
```

**`Book.java`**

```java
@Getter
@Setter
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}
```

-----

### 3\. Many-to-Many (Simple): `Product`  ←→ `Category` 

A standard many-to-many relationship where a `Product` can belong to multiple `Category` entities, and a `Category` can contain multiple `Product` entities. This requires an intermediate join table.

* **Owning Side**: `Product`. This side defines the relationship's structure using `@JoinTable`.
    * `@JoinTable(name = "product_category", ...)` specifies that a table named `product_category` will be created to manage the link.
    * `joinColumns` points to the `product_id` foreign key.
    * `inverseJoinColumns` points to the `category_id` foreign key.
* **Inverse Side**: `Category`. It simply uses `@ManyToMany(mappedBy = "categories")` to reference the relationship configured in the `Product` entity.
* **Best Practice**: Helper methods like `addCategory()` and `removeCategory()` are crucial for synchronizing both sides of the relationship (`product.getCategories()` and `category.getProducts()`) simultaneously.

#### Code Examples

**`Product.java`**

```java
@Getter
@Setter
@Entity
@ToString(exclude = "categories")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getProducts().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getProducts().remove(this);
    }
}
```

**`Category.java`**

```java
@Getter
@Setter
@Entity
@ToString(exclude = "products")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();
}
```

-----

### 4\. Many-to-Many with Extra Attributes: `Student`  ←→ `Course`  (with `Grade` )

This is the standard pattern when you need to store additional data on the relationship itself (e.g., the `grade` a `Student` received in a `Course`). A simple `@ManyToMany` is not sufficient. The solution is to model the join table as its own entity, called `Enrollment`.

* **The Breakdown**: The `@ManyToMany` relationship is broken down into two one-to-many relationships:
    1.  `Student` ← `@OneToMany` → `Enrollment`
    2.  `Course` ← `@OneToMany` → `Enrollment`
* **The Join Entity**: `Enrollment`.
    * It holds the extra attribute (`grade`).
    * It has `@ManyToOne` relationships back to both `Student` and `Course`.
    * It uses a **composite primary key** (`@IdClass`) composed of the `student` and `course` IDs to ensure a student can only enroll in the same course once.
* **Best Practice**: The `Student.addCourse(course, grade)` helper method abstracts away the creation of the `Enrollment` entity, providing a clean API to establish the relationship while setting the extra attribute.

#### Code Examples

**`Student.java`**

```java
@Getter
@Setter
@Entity
@ToString(exclude = "enrollments")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();

    public void addCourse(Course course, String grade) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(this);
        enrollment.setCourse(course);
        enrollment.setGrade(grade);

        this.enrollments.add(enrollment);
        course.getEnrollments().add(enrollment);
    }
}
```

**`Course.java`**

```java
@Getter
@Setter
@Entity
@ToString(exclude = "enrollments")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();
}
```

**`Enrollment.java`**

```java
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
    private String grade;
}
```

**`EnrollmentId.java`**

```java
@EqualsAndHashCode
public class EnrollmentId implements Serializable {
    private Long student;
    private Long course;
}
```