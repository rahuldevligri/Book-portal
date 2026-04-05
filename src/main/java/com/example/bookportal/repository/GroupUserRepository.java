package com.example.bookportal.repository;

import com.example.bookportal.entity.GroupUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GroupUserRepository extends JpaRepository<GroupUserEntity, Long> {
    /**
     * Finds GroupUserEntity mappings for a specific user ID.
     *
     * @param userId the user ID
     * @return list of GroupUserEntity mappings
     */
    @Query("SELECT g FROM GroupUserEntity g WHERE g.user.id = :userId")
    List<GroupUserEntity> findByUserId(@Param("userId") Long userId);

    /**
     * Finds GroupUserEntity mappings for multiple user IDs, fetching associated
     * groups.
     *
     * @param userIds list of user IDs
     * @return list of GroupUserEntity mappings
     */
    @Query("SELECT DISTINCT g FROM GroupUserEntity g JOIN FETCH g.group WHERE g.user.id IN :userIds")
    List<GroupUserEntity> findByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * Returns assigned group IDs for a user.
     *
     * @param userId user ID
     * @return group IDs
     */
    @Query("SELECT g.group.id FROM GroupUserEntity g WHERE g.user.id = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    /**
     * Deletes GroupUserEntity mappings for a specific group ID.
     *
     * @param groupId the group ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupUserEntity g WHERE g.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);

    /**
     * Deletes a single user-group mapping for a specific user ID and group ID.
     *
     * @param userId  the user ID
     * @param groupId the group ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupUserEntity g WHERE g.user.id = :userId AND g.group.id = :groupId")
    void deleteByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
}


