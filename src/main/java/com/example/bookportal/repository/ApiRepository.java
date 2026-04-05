package com.example.bookportal.repository;

import com.example.bookportal.entity.ApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiRepository extends JpaRepository<ApiEntity, Long> {
	/**
	 * Finds an API entity by its URL, ignoring case.
	 *
	 * @param apiUrl the API URL
	 * @return an Optional containing the API entity if found
	 */
	Optional<ApiEntity> findByApiUrlIgnoreCase(String apiUrl);

	/**
	 * Checks if an API URL exists for a different ID, ignoring case.
	 *
	 * @param apiUrl the API URL
	 * @param id     the ID to exclude
	 * @return true if exists, false otherwise
	 */
	boolean existsByApiUrlIgnoreCaseAndIdNot(String apiUrl, Long id);

	/**
	 * Returns API URL by ID.
	 *
	 * @param id API ID
	 * @return optional API URL
	 */
	@Query("SELECT a.apiUrl FROM ApiEntity a WHERE a.id = :id")
	Optional<String> findApiUrlById(@Param("id") Long id);
}


