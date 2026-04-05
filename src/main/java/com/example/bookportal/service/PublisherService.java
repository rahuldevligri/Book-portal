package com.example.bookportal.service;

import com.example.bookportal.dto.PublisherDTO;
import com.example.bookportal.entity.PublisherEntity;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing publishers and distributors.
 */
public interface PublisherService {
    /**
     * Retrieves paginated publishers.
     *
     * @param pageable pagination information
     * @return page of publishers
     */
    Page<PublisherEntity> getPublishersPage(Pageable pageable);

    /**
     * Retrieves paginated publishers filtered by search text and match type.
     *
     * @param query search text
     * @param matchType match type (exact/start/contains)
     * @param pageable pagination information
     * @return page of matching publishers
     */
    Page<PublisherEntity> searchPublishers(String query, String matchType, Pageable pageable);

    /**
     * Retrieves all publishers for dropdown/select use cases.
     *
     * @return list of publishers
     */
    List<PublisherEntity> getAllPublishers();

    /**
     * Retrieves paginated publishers/distributors by entity type id.
     *
     * @param entityTypeId entity type id
     * @param pageable     pagination information
     * @return page of matching publishers
     */
    Page<PublisherEntity> getPublishersByType(Long entityTypeId, Pageable pageable);

    /**
     * Retrieves a publisher by ID.
     *
     * @param id publisher ID
     * @return publisher entity
     */
    PublisherEntity getPublisherById(Long id);

    /**
     * Retrieves category-wise book counts for a publisher.
     *
     * @param id publisher ID
     * @return list of category book count projections
     */
    List<CategoryBookCountProjection> getCategoryWiseBooks(Long id);

    /**
     * Retrieves the total book count for a publisher.
     *
     * @param id publisher ID
     * @return number of books
     */
    long getPublisherBookCount(Long id);

    /**
     * Retrieves paginated books for a publisher.
     *
     * @param publisherId publisher ID
     * @param pageable    pagination information
     * @return page of books
     */
    Page<com.example.bookportal.entity.BookEntity> getBooksByPublisher(Long publisherId, Pageable pageable);

    /**
     * Creates a publisher/distributor with the given entity type.
     *
     * @param publisher    publisher entity
     * @param entityTypeId entity type id
     * @return created publisher
     */
    PublisherEntity createPublisher(PublisherEntity publisher, Long entityTypeId);

    /**
     * Updates an existing publisher/distributor.
     *
     * @param id        publisher ID
     * @param publisher updated data
     * @return updated publisher
     */
    PublisherEntity updatePublisher(Long id, PublisherEntity publisher);

    /**
     * Converts partner DTO to publisher entity.
     *
     * @param dto source DTO
     * @return mapped entity
     */
    PublisherEntity fromDto(PublisherDTO dto);

    /**
     * Converts publisher entity to DTO.
     *
     * @param entity source entity
     * @return mapped DTO
     */
    PublisherDTO toDto(PublisherEntity entity);

    /**
     * Checks if incoming DTO changes persisted partner data.
     *
     * @param existing persisted entity
     * @param dto      incoming DTO
     * @return true when values differ, false otherwise
     */
    boolean hasChanges(PublisherEntity existing, PublisherDTO dto);

    /**
     * Deletes publishers/distributors by IDs.
     *
     * @param ids list of IDs
     */
    void deletePublishers(List<Long> ids);
}
