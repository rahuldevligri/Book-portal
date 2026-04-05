package com.example.bookportal.service;

import com.example.bookportal.dto.GroupApisResponse;
import com.example.bookportal.dto.ApiDTO;
import com.example.bookportal.dto.GroupPrivilegesDTO;
import com.example.bookportal.dto.UserGroupsResponse;
import com.example.bookportal.dto.UserPrivilegesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrivilegesService {
        /**
         * Sanitizes a list of positive IDs for a given entity type.
         *
         * @param ids list of IDs to sanitize
         * @param entityType type of entity (e.g., "userIds", "groupIds", "apiIds")
         * @param strict whether to enforce strict validation
         * @return sanitized list of positive IDs
         */
        List<Long> sanitizePositiveIds(List<Long> ids, String entityType, boolean strict);

        /**
         * Sanitizes a list of positive group IDs.
         *
         * @param groupIds list of group IDs to sanitize
         * @return sanitized list of positive group IDs
         */
        List<Long> sanitizePositiveIdsForGroups(List<Long> groupIds);
    /**
     * Retrieves paginated user privileges.
     *
     * @param search   search term
     * @param groupId  group ID
     * @param pageable pagination information
     * @return page of user privileges
     */
    Page<UserPrivilegesDTO> listUsers(String search, Long groupId, Pageable pageable);

    /**
     * Retrieves paginated group privileges.
     *
     * @param search   search term
     * @param pageable pagination information
     * @return page of group privileges
     */
    Page<GroupPrivilegesDTO> listGroups(String search, Pageable pageable);

    /**
     * Lists all groups for management.
     *
     * @return list of group privileges DTOs
     */
    List<GroupPrivilegesDTO> listAllGroupsForManagement();

    /**
     * Creates a new group.
     *
     * @param groupName the group name
     * @return created group privileges DTO
     */
    GroupPrivilegesDTO createGroup(String groupName);

    /**
     * Updates an existing group.
     *
     * @param groupId   the group ID
     * @param groupName the group name
     * @return updated group privileges DTO
     */
    GroupPrivilegesDTO updateGroup(Long groupId, String groupName);

    /**
     * Deletes groups by their IDs.
     *
     * @param groupIds list of group IDs
     */
    void deleteGroups(List<Long> groupIds);

    /**
     * Retrieves paginated APIs for management.
     *
     * @param pageable pagination information
     * @return page of API DTOs
     */
    Page<ApiDTO> listAllApisForManagement(Pageable pageable);

    /**
     * Retrieves paginated APIs assigned to a specific group.
     *
     * @param groupId   selected group ID
     * @param pageable  pagination and sorting
     * @return page of APIs assigned to the group
     */
    Page<ApiDTO> listApisForGroup(Long groupId, Pageable pageable);

    /**
     * Retrieves all APIs for assignment flows in a stable sort order.
     *
     * @return list of API DTOs
     */
    List<ApiDTO> listAllApisForAssignment();

    /**
     * Creates a new API.
     *
     * @param apiUrl the API URL
     * @return created API DTO
     */
    ApiDTO createApi(String apiUrl);

    /**
     * Updates an existing API.
     *
     * @param apiId  the API ID
     * @param apiUrl the API URL
     * @return updated API DTO
     */
    ApiDTO updateApi(Long apiId, String apiUrl);

    /**
     * Deletes APIs by their IDs.
     *
     * @param apiIds list of API IDs
     */
    void deleteApis(List<Long> apiIds);

    /**
     * Retrieves user groups for a user.
     *
     * @param userId the user ID
     * @return user groups response
     */
    UserGroupsResponse getUserGroups(Long userId);

    /**
     * Updates user groups for a user.
     *
     * @param userId   the user ID
     * @param groupIds list of group IDs
     */
    void updateUserGroups(Long userId, java.util.List<Long> groupIds);

    /**
     * Retrieves group APIs for a group.
     *
     * @param groupId the group ID
     * @return group APIs response
     */
    GroupApisResponse getGroupApis(Long groupId);

    /**
     * Updates group APIs for a group.
     *
     * @param groupId the group ID
     * @param apiIds  list of API IDs
     */
    void updateGroupApis(Long groupId, java.util.List<Long> apiIds);

        // Utility and business logic methods moved from AdminPrivilegesController

    boolean isSameGroupName(Long groupId, String groupName);

    boolean isSameApiUrl(Long apiId, String apiUrl);

    boolean isSameUserGroupAssignment(Long userId, List<Long> groupIds);

    boolean isSameGroupApiAssignment(Long groupId, List<Long> apiIds);

    UserPrivilegesDTO mapToUserPrivilegesDTO(com.example.bookportal.entity.UserEntity user);

    String msg(String key);

    /**
     * Resolves the default group selection for privileges screens.
     * Prefers "Admin" group when available.
     *
     * @return default group ID or null when no groups exist
     */
    Long resolveDefaultGroupId();
}
