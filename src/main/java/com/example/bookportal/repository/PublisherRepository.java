package com.example.bookportal.repository;

import com.example.bookportal.entity.PublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PublisherRepository extends JpaRepository<PublisherEntity, Long> {
    /**
     * Finds publishers by entity type ID with pagination.
     *
     * @param entityTypeId the entity type ID
     * @param pageable     pagination information
     * @return page of PublisherEntity objects
     */
    Page<PublisherEntity> findByEntityTypeId(Long entityTypeId, Pageable pageable);

    /**
     * Returns publishers whose name matches the provided LIKE pattern.
     *
     * @param like the LIKE pattern for search
     * @return list of matching publishers
     */
    @Query(value = "SELECT * FROM publisher_distributor p WHERE LOWER(p.NAME) LIKE :like", nativeQuery = true)
    java.util.List<PublisherEntity> findMatchingForSearch(@Param("like") String like);

    /**
     * Returns distinct matching publisher names for search suggestions.
     *
     * @param like the LIKE pattern for search
     * @return list of distinct publisher names
     */
    @Query(value = """
            SELECT DISTINCT TRIM(p.NAME) AS name
            FROM publisher_distributor p
            WHERE LOWER(p.NAME) LIKE :like
            AND TRIM(p.NAME) <> ''
            ORDER BY name
            LIMIT 100
            """, nativeQuery = true)
    java.util.List<String> findDistinctPublisherNamesForSearch(@Param("like") String like);

}


