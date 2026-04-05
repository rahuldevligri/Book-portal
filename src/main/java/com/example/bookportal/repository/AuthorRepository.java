package com.example.bookportal.repository;

import com.example.bookportal.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
	/**
	 * Returns authors whose first, last, or full name matches the provided LIKE
	 * pattern.
	 * Used to show multiple matching author groups without scanning the full table.
	 *
	 * @param like the LIKE pattern for search
	 * @return list of matching authors
	 */
	@Query(value = """
				SELECT *
				FROM author a
				WHERE (
					LOWER(a.FIRST_NAME) LIKE :like
					OR LOWER(a.LAST_NAME) LIKE :like
					OR LOWER(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) LIKE :like
					OR LOWER(CONCAT(a.FIRST_NAME, a.LAST_NAME)) LIKE :like
				)
			""", nativeQuery = true)
	List<AuthorEntity> findMatchingForSearch(@Param("like") String like);

	/**
	 * Returns distinct matching author display names for search suggestions.
	 *
	 * @param like the LIKE pattern for search
	 * @return list of distinct author names
	 */
	@Query(value = """
				SELECT DISTINCT TRIM(CONCAT(COALESCE(a.FIRST_NAME, ''), ' ', COALESCE(a.LAST_NAME, ''))) AS authorName
				FROM author a
				WHERE (
					LOWER(a.FIRST_NAME) LIKE :like
					OR LOWER(a.LAST_NAME) LIKE :like
					OR LOWER(CONCAT(a.FIRST_NAME, ' ', a.LAST_NAME)) LIKE :like
					OR LOWER(CONCAT(a.FIRST_NAME, a.LAST_NAME)) LIKE :like
				)
				AND TRIM(CONCAT(COALESCE(a.FIRST_NAME, ''), ' ', COALESCE(a.LAST_NAME, ''))) <> ''
				ORDER BY authorName
				LIMIT 100
			""", nativeQuery = true)
	List<String> findDistinctAuthorNamesForSearch(@Param("like") String like);

}


