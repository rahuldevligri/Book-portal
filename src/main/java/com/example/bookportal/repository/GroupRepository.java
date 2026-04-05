package com.example.bookportal.repository;

import com.example.bookportal.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
	/**
	 * Finds a group entity by its name, ignoring case.
	 *
	 * @param groupName the group name
	 * @return an Optional containing the group entity if found
	 */
	Optional<GroupEntity> findByGroupNameIgnoreCase(String groupName);

	/**
	 * Checks if a group name exists for a different ID, ignoring case.
	 *
	 * @param groupName the group name
	 * @param id        the ID to exclude
	 * @return true if exists, false otherwise
	 */
	boolean existsByGroupNameIgnoreCaseAndIdNot(String groupName, Long id);

	/**
	 * Returns group name by ID.
	 *
	 * @param id group ID
	 * @return optional group name
	 */
	@Query("SELECT g.groupName FROM GroupEntity g WHERE g.id = :id")
	Optional<String> findGroupNameById(@Param("id") Long id);
}


