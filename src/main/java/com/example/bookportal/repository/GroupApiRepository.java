package com.example.bookportal.repository;

import com.example.bookportal.entity.GroupApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GroupApiRepository extends JpaRepository<GroupApiEntity, Long> {
    /**
     * Finds GroupApiEntity mappings for a specific group ID.
     *
     * @param groupId the group ID
     * @return list of GroupApiEntity mappings
     */
    @Query("SELECT g FROM GroupApiEntity g WHERE g.group.id = :groupId")
    List<GroupApiEntity> findByGroupId(@Param("groupId") Long groupId);

    /**
     * Finds GroupApiEntity mappings for multiple group IDs, fetching associated
     * APIs.
     *
     * @param groupIds list of group IDs
     * @return list of GroupApiEntity mappings
     */
    @Query("SELECT DISTINCT g FROM GroupApiEntity g JOIN FETCH g.api WHERE g.group.id IN :groupIds")
    List<GroupApiEntity> findByGroupIds(@Param("groupIds") List<Long> groupIds);

    /**
     * Returns assigned API IDs for a group.
     *
     * @param groupId group ID
     * @return API IDs
     */
    @Query("SELECT g.api.id FROM GroupApiEntity g WHERE g.group.id = :groupId")
    List<Long> findApiIdsByGroupId(@Param("groupId") Long groupId);

    /**
     * Deletes GroupApiEntity mappings for a specific group ID.
     *
     * @param groupId the group ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupApiEntity g WHERE g.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);

    /**
     * Deletes GroupApiEntity mappings for a list of API IDs.
     *
     * @param apiIds list of API IDs
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupApiEntity g WHERE g.api.id IN :apiIds")
    void deleteByApiIds(@Param("apiIds") List<Long> apiIds);

    /**
     * Deletes a GroupApiEntity mapping for a specific group ID and API ID.
     *
     * @param groupId the group ID
     * @param apiId   the API ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupApiEntity g WHERE g.group.id = :groupId AND g.api.id = :apiId")
    void deleteByGroupIdAndApiId(@Param("groupId") Long groupId, @Param("apiId") Long apiId);
}


