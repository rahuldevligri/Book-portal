package com.example.bookportal.repository;

import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;
import com.example.bookportal.repository.projection.SearchBookSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {

    // ================= CATEGORY COUNT =================

    @Query(value = """
        SELECT 
            bc.ID AS categoryId,
            bc.CATEGORY AS categoryName,
            COUNT(DISTINCT b.ID) AS bookCount
        FROM book b
        JOIN book_category bc ON b.BOOK_CATEGORY_ID = bc.ID
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        WHERE bp.PUBLISHER_ID = :publisherId
        GROUP BY bc.ID, bc.CATEGORY
    """, nativeQuery = true)
    List<CategoryBookCountProjection> findCategoryWiseBookCountByPublisher(@Param("publisherId") Long publisherId);

    @Query(value = """
        SELECT 
            bc.ID AS categoryId,
            bc.CATEGORY AS categoryName,
            COUNT(DISTINCT b.ID) AS bookCount
        FROM book b
        JOIN book_category bc ON b.BOOK_CATEGORY_ID = bc.ID
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        WHERE ba.AUTHOR_ID = :authorId
        GROUP BY bc.ID, bc.CATEGORY
    """, nativeQuery = true)
    List<CategoryBookCountProjection> findCategoryWiseBookCountByAuthor(@Param("authorId") Long authorId);

    // ================= FILTER QUERIES =================

    @Query(value = """
        SELECT DISTINCT b.* FROM book b
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        WHERE ba.AUTHOR_ID = :authorId
    """,
            countQuery = """
        SELECT COUNT(DISTINCT b.ID) FROM book b
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        WHERE ba.AUTHOR_ID = :authorId
    """,
            nativeQuery = true)
    Page<BookEntity> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT b.* FROM book b
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        WHERE bp.PUBLISHER_ID = :publisherId
    """,
            countQuery = """
        SELECT COUNT(DISTINCT b.ID) FROM book b
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        WHERE bp.PUBLISHER_ID = :publisherId
    """,
            nativeQuery = true)
    Page<BookEntity> findByPublisher(@Param("publisherId") Long publisherId, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT b.* FROM book b
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        WHERE ba.AUTHOR_ID = :authorId AND b.BOOK_CATEGORY_ID = :categoryId
    """,
            countQuery = """
        SELECT COUNT(DISTINCT b.ID) FROM book b
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        WHERE ba.AUTHOR_ID = :authorId AND b.BOOK_CATEGORY_ID = :categoryId
    """,
            nativeQuery = true)
    Page<BookEntity> findByAuthorAndCategory(@Param("authorId") Long authorId,
                                             @Param("categoryId") Long categoryId,
                                             Pageable pageable);

    @Query(value = """
        SELECT DISTINCT b.* FROM book b
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        WHERE bp.PUBLISHER_ID = :publisherId AND b.BOOK_CATEGORY_ID = :categoryId
    """,
            countQuery = """
        SELECT COUNT(DISTINCT b.ID) FROM book b
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        WHERE bp.PUBLISHER_ID = :publisherId AND b.BOOK_CATEGORY_ID = :categoryId
    """,
            nativeQuery = true)
    Page<BookEntity> findByPublisherAndCategory(@Param("publisherId") Long publisherId,
                                                @Param("categoryId") Long categoryId,
                                                Pageable pageable);

    Page<BookEntity> findByBookCategoryId(Long bookCategoryId, Pageable pageable);

    long countByBookCategoryId(Long bookCategoryId);

    @Query(value = """
        SELECT COUNT(DISTINCT b.ID) FROM book b
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        WHERE bp.PUBLISHER_ID = :publisherId
    """, nativeQuery = true)
    long countByPublisher(@Param("publisherId") Long publisherId);

    // ================= OPTIMIZED AUTHOR SEARCH =================

    @Query(value = """
        SELECT
            b.ID AS id,
            b.TITLE AS title,
            CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME) AS authorName,
            bc.CATEGORY AS categoryName,
            COALESCE(MIN(pd.NAME), '') AS publisherName,
            b.IMAGE_PATH AS imagePath,
            b.THUMBNAIL_PATH AS thumbnailPath
        FROM book b
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        JOIN author a ON a.ID = ba.AUTHOR_ID
        JOIN book_category bc ON bc.ID = b.BOOK_CATEGORY_ID
        LEFT JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        LEFT JOIN publisher_distributor pd ON pd.ID = bp.PUBLISHER_ID
        WHERE (
            (:exact = true AND (
                LOWER(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) = :query
                OR LOWER(CONCAT(a.FIRST_NAME, a.LAST_NAME)) = :query
                OR ((a.LAST_NAME IS NULL OR a.LAST_NAME = '') AND LOWER(a.FIRST_NAME) = :query)
            ))
            OR
            (:exact = false AND (
                LOWER(a.FIRST_NAME) LIKE :query
                OR LOWER(a.LAST_NAME) LIKE :query
                OR LOWER(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) LIKE :query
                OR LOWER(CONCAT(a.FIRST_NAME, a.LAST_NAME)) LIKE :query
            ))
        )
        GROUP BY b.ID, b.TITLE, a.ID, a.FIRST_NAME, a.LAST_NAME, bc.CATEGORY, b.IMAGE_PATH, b.THUMBNAIL_PATH
    """,
            countQuery = """
        SELECT COUNT(DISTINCT CONCAT(b.ID, '-', a.ID))
        FROM book b
        JOIN book_author ba ON ba.BOOK_ID = b.ID
        JOIN author a ON a.ID = ba.AUTHOR_ID
        WHERE (
            (:exact = true AND (
                LOWER(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) = :query
                OR LOWER(CONCAT(a.FIRST_NAME, a.LAST_NAME)) = :query
                OR ((a.LAST_NAME IS NULL OR a.LAST_NAME = '') AND LOWER(a.FIRST_NAME) = :query)
            ))
            OR
            (:exact = false AND (
                LOWER(a.FIRST_NAME) LIKE :query
                OR LOWER(a.LAST_NAME) LIKE :query
                OR LOWER(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) LIKE :query
                OR LOWER(CONCAT(a.FIRST_NAME, a.LAST_NAME)) LIKE :query
            ))
        )
    """,
            nativeQuery = true)
    Page<SearchBookSummaryProjection> searchAuthor(
            @Param("query") String query,
            @Param("exact") boolean exact,
            Pageable pageable);

    // ================= OPTIMIZED PUBLISHER SEARCH =================

    @Query(value = """
        SELECT
            b.ID AS id,
            b.TITLE AS title,
            MIN(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) AS authorName,
            bc.CATEGORY AS categoryName,
            pd.NAME AS publisherName,
            b.IMAGE_PATH AS imagePath,
            b.THUMBNAIL_PATH AS thumbnailPath
        FROM book b
        JOIN book_category bc ON bc.ID = b.BOOK_CATEGORY_ID
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        JOIN publisher_distributor pd ON pd.ID = bp.PUBLISHER_ID
        LEFT JOIN book_author ba ON ba.BOOK_ID = b.ID
        LEFT JOIN author a ON a.ID = ba.AUTHOR_ID
        WHERE (
            (:exact = true AND LOWER(pd.NAME) = :query)
            OR
            (:exact = false AND LOWER(pd.NAME) LIKE :query)
        )
        GROUP BY b.ID, b.TITLE, bc.CATEGORY, pd.NAME, b.IMAGE_PATH, b.THUMBNAIL_PATH
    """,
            countQuery = """
        SELECT COUNT(DISTINCT b.ID)
        FROM book b
        JOIN book_publisher bp ON bp.BOOK_ID = b.ID
        JOIN publisher_distributor pd ON pd.ID = bp.PUBLISHER_ID
        WHERE (
            (:exact = true AND LOWER(pd.NAME) = :query)
            OR
            (:exact = false AND LOWER(pd.NAME) LIKE :query)
        )
    """,
            nativeQuery = true)
    Page<SearchBookSummaryProjection> searchPublisher(
            @Param("query") String query,
            @Param("exact") boolean exact,
            Pageable pageable);
}
