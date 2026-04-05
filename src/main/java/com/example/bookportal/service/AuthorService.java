package com.example.bookportal.service;

import com.example.bookportal.dto.AuthorDTO;
import com.example.bookportal.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing authors and their related operations.
 */
public interface AuthorService {

    /**
     * Retrieves paginated authors.
     *
     * @param pageable pagination information
     * @return page of authors
     */
    Page<AuthorEntity> getAuthorsPage(Pageable pageable);

    /**
     * Retrieves paginated authors filtered by search text and match type.
     *
     * @param query search text
     * @param matchType match type (exact/start/contains)
     * @param pageable pagination information
     * @return page of matching authors
     */
    Page<AuthorEntity> searchAuthors(String query, String matchType, Pageable pageable);

    /**
     * Retrieves an author by ID.
     *
     * @param authorId ID of the author
     * @return author entity
     */
    AuthorEntity getAuthorById(Long authorId);

    /**
     * Retrieves paginated books for an author.
     *
     * @param authorId ID of the author
     * @param pageable pagination information
     * @return page of books
     */
    Page<com.example.bookportal.entity.BookEntity> getBooksByAuthor(Long authorId, Pageable pageable);

    /**
     * Creates a new author.
     *
     * @param author author entity
     * @return persisted author
     */
    AuthorEntity createAuthor(AuthorEntity author);

    /**
     * Updates an existing author.
     *
     * @param id     author id
     * @param author updated data
     * @return updated author
     */
    AuthorEntity updateAuthor(Long id, AuthorEntity author);

    /**
     * Converts author DTO to entity for persistence operations.
     *
     * @param dto source DTO
     * @return mapped entity
     */
    AuthorEntity fromDto(AuthorDTO dto);

    /**
     * Converts author entity to DTO for form operations.
     *
     * @param entity source entity
     * @return mapped DTO
     */
    AuthorDTO toDto(AuthorEntity entity);

    /**
     * Checks if the edit request contains meaningful changes.
     *
     * @param existing persisted author
     * @param dto      incoming edit payload
     * @return true when values differ, false when effectively unchanged
     */
    boolean hasChanges(AuthorEntity existing, AuthorDTO dto);

    /**
     * Deletes authors by IDs.
     *
     * @param authorIds list of author IDs
     */
    void deleteAuthors(List<Long> authorIds);

    /**
     * Retrieves category-wise book count for an author.
     *
     * @param authorId ID of the author
     * @return list of category book count projections
     */
    java.util.List<com.example.bookportal.repository.projection.CategoryBookCountProjection> getCategoryWiseBooks(
            Long authorId);

}
