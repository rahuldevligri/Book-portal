package com.example.bookportal.repository;

import com.example.bookportal.entity.Book;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = """
        SELECT 
            c.category_id   AS categoryId,
            c.category_name AS categoryName,
            COUNT(b.book_id) AS bookCount
        FROM books b
        JOIN categories c ON b.category_id = c.category_id
        WHERE b.author_id = :authorId
        GROUP BY c.category_id, c.category_name
    """, nativeQuery = true)
    List<CategoryBookCountProjection> findCategoryWiseBookCountByAuthor(
            @Param("authorId") Long authorId
    );
    List<Book> findByAuthorAuthorIdAndCategoryCategoryId(
            Long authorId,
            Long categoryId
    );
}
