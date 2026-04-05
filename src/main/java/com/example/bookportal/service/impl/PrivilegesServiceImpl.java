package com.example.bookportal.service.impl;

import com.example.bookportal.dto.GroupApisResponse;
import com.example.bookportal.dto.ApiDTO;
import com.example.bookportal.dto.GroupPrivilegesDTO;
import com.example.bookportal.dto.UserGroupsResponse;
import com.example.bookportal.dto.UserPrivilegesDTO;
import com.example.bookportal.entity.*;
import com.example.bookportal.repository.ApiRepository;
import com.example.bookportal.repository.GroupApiRepository;
import com.example.bookportal.repository.GroupRepository;
import com.example.bookportal.repository.GroupUserRepository;
import com.example.bookportal.repository.UserRepository;
import com.example.bookportal.service.PrivilegesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrivilegesServiceImpl implements PrivilegesService {

    private static final Logger logger = LoggerFactory.getLogger(PrivilegesServiceImpl.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private GroupApiRepository groupApiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    private final TransactionTemplate txTemplate;

    @Autowired
    public PrivilegesServiceImpl(PlatformTransactionManager txManager) {
        this.txTemplate = new TransactionTemplate(txManager);
    }

    /**
     * Sanitizes a list of positive IDs for a given entity type.
     * Removes nulls, non-positive values, and duplicates.
     *
     * @param ids list of IDs to sanitize
     * @param entityType type of entity (e.g., "userIds", "groupIds", "apiIds")
     * @param strict whether to enforce strict validation
     * @return sanitized list of positive IDs
     */
    @Override
    public List<Long> sanitizePositiveIds(List<Long> ids, String entityType, boolean strict) {
        // Basic sanitization: remove nulls, non-positive, duplicates
        List<Long> sanitized = uniquePositiveIds(ids);
        // Optionally enforce strict validation (e.g., throw if empty)
        if (strict && sanitized.isEmpty()) {
            throw new IllegalArgumentException("No valid " + entityType + " provided.");
        }
        return sanitized;
    }

    /**
     * Sanitizes a list of positive group IDs.
     * Removes nulls, non-positive values, and duplicates.
     *
     * @param groupIds list of group IDs to sanitize
     * @return sanitized list of positive group IDs
     */
    @Override
    public List<Long> sanitizePositiveIdsForGroups(List<Long> groupIds) {
        return uniquePositiveIds(groupIds);
    }

    /**
     * Retrieves paginated user privileges.
     *
     * @param search   search term
     * @param groupId  group ID
     * @param pageable pagination information
     * @return page of user privileges
     */
    @Override
    public Page<UserPrivilegesDTO> listUsers(String search, Long groupId, Pageable pageable) {
        logger.debug("listUsers called: search='{}', groupId={}, page={}, size={}", search, groupId,
                pageable.getPageNumber(), pageable.getPageSize());
        Page<UserEntity> usersPage;
        if (groupId != null) {
            usersPage = userRepository.findByGroupId(groupId, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        List<Long> userIds = usersPage.getContent().stream().map(UserEntity::getId).toList();
        Map<Long, List<GroupUserEntity>> fetchedGroupsByUserId = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            fetchedGroupsByUserId = groupUserRepository.findByUserIds(userIds)
                    .stream()
                    .collect(Collectors.groupingBy(gu -> gu.getUser().getId()));
        }
        final Map<Long, List<GroupUserEntity>> groupsByUserId = fetchedGroupsByUserId;

        List<UserPrivilegesDTO> dtos = usersPage.getContent().stream().map(u -> {
            UserPrivilegesDTO d = new UserPrivilegesDTO();
            d.setId(u.getId());
            d.setFirstName(u.getFirstName());
            d.setLastName(u.getLastName());
            d.setUserName(u.getUserName());
            List<GroupUserEntity> gus = groupsByUserId.getOrDefault(u.getId(), List.of());
            List<UserPrivilegesDTO.GroupInfo> groups = gus.stream().map(gu -> {
                UserPrivilegesDTO.GroupInfo gi = new UserPrivilegesDTO.GroupInfo();
                gi.setId(gu.getGroup().getId());
                gi.setGroupName(gu.getGroup().getGroupName());
                return gi;
            }).collect(Collectors.toList());
            d.setGroups(groups);
            return d;
        }).collect(Collectors.toList());
        logger.debug("listUsers returning {} users (totalElements={})", dtos.size(), usersPage.getTotalElements());
        return new PageImpl<>(dtos, pageable, usersPage.getTotalElements());
    }

    /**
     * Retrieves paginated group privileges.
     *
     * @param search   search term
     * @param pageable pagination information
     * @return page of group privileges
     */
    @Override
    public Page<GroupPrivilegesDTO> listGroups(String search, Pageable pageable) {
        logger.debug("listGroups called: search='{}', page={}, size={}", search, pageable.getPageNumber(),
                pageable.getPageSize());
        Page<GroupEntity> gp = groupRepository.findAll(pageable);

        List<Long> groupIds = gp.getContent().stream().map(GroupEntity::getId).toList();
        Map<Long, List<String>> apisByGroupId = new HashMap<>();
        if (!groupIds.isEmpty()) {
            for (GroupApiEntity ga : groupApiRepository.findByGroupIds(groupIds)) {
                Long gid = ga.getGroup().getId();
                apisByGroupId.computeIfAbsent(gid, key -> new ArrayList<>()).add(ga.getApi().getApiUrl());
            }
        }

        List<GroupPrivilegesDTO> dtos = gp.getContent().stream()
                .map(g -> toGroupPrivilegesDto(g, apisByGroupId.getOrDefault(g.getId(), List.of())))
                .collect(Collectors.toList());
        logger.debug("listGroups returning {} groups (totalElements={})", dtos.size(), gp.getTotalElements());
        return new PageImpl<>(dtos, pageable, gp.getTotalElements());
    }

    /**
     * Lists all groups for management.
     *
     * @return list of group privileges DTOs
     */
    @Override
    public List<GroupPrivilegesDTO> listAllGroupsForManagement() {
        return groupRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(g -> toGroupPrivilegesDto(g, List.of()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new group.
     *
     * @param groupName the group name
     * @return created group privileges DTO
     */
    @Override
    public GroupPrivilegesDTO createGroup(String groupName) {
        String normalizedName = normalizeGroupName(groupName);
        if (groupRepository.findByGroupNameIgnoreCase(normalizedName).isPresent()) {
            logger.warn("Attempt to create duplicate group: {}", normalizedName);
            throw new IllegalArgumentException("Group name already exists");
        }
        GroupEntity entity = new GroupEntity();
        entity.setGroupName(normalizedName);
        GroupEntity saved = groupRepository.save(entity);
        logger.info("Group created: id={}, name={}", saved.getId(), saved.getGroupName());
        return toGroupPrivilegesDto(saved, List.of());
    }

    /**
     * Updates an existing group.
     *
     * @param groupId   the group ID
     * @param groupName the group name
     * @return updated group privileges DTO
     */
    @Override
    public GroupPrivilegesDTO updateGroup(Long groupId, String groupName) {
        String normalizedName = normalizeGroupName(groupName);
        GroupEntity entity = groupRepository.findById(groupId).orElseThrow();
        if (groupRepository.existsByGroupNameIgnoreCaseAndIdNot(normalizedName, groupId)) {
            logger.warn("Attempt to update group to duplicate name: {}", normalizedName);
            throw new IllegalArgumentException("Group name already exists");
        }
        entity.setGroupName(normalizedName);
        GroupEntity saved = groupRepository.save(entity);
        logger.info("Group updated: id={}, name={}", saved.getId(), saved.getGroupName());
        return toGroupPrivilegesDto(saved, List.of());
    }

    /**
     * Deletes groups by their IDs.
     *
     * @param groupIds list of group IDs
     */
    @Override
    public void deleteGroups(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return;
        }
        for (Long groupId : groupIds) {
            groupApiRepository.deleteByGroupId(groupId);
            groupUserRepository.deleteByGroupId(groupId);
            logger.info("Deleted group associations for groupId={}", groupId);
        }
        groupRepository.deleteAllById(groupIds);
        logger.info("Groups deleted: {}", groupIds);
    }

    /**
     * Retrieves paginated APIs for management.
     *
     * @param pageable pagination information
     * @return page of API DTOs
     */
    @Override
    public Page<ApiDTO> listAllApisForManagement(Pageable pageable) {
        Page<ApiEntity> apiPage = apiRepository.findAll(pageable);
        List<ApiDTO> dtos = apiPage.getContent().stream()
                .map(this::toApiDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, apiPage.getTotalElements());
    }

    @Override
    public Page<ApiDTO> listApisForGroup(Long groupId, Pageable pageable) {
        if (groupId == null) {
            return Page.empty(pageable);
        }

        List<Long> apiIds = groupApiRepository.findApiIdsByGroupId(groupId);
        if (apiIds == null || apiIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<ApiDTO> dtos = apiRepository.findAllById(apiIds).stream()
                .map(this::toApiDto)
                .collect(Collectors.toList());

        Comparator<ApiDTO> comparator = buildApiComparator(pageable);
        dtos.sort(comparator);

        int total = dtos.size();
        int from = (int) pageable.getOffset();
        if (from >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }
        int to = Math.min(from + pageable.getPageSize(), total);
        return new PageImpl<>(dtos.subList(from, to), pageable, total);
    }

    @Override
    public List<ApiDTO> listAllApisForAssignment() {
        return apiRepository.findAll(Sort.by(Sort.Direction.ASC, "apiUrl"))
                .stream()
                .map(this::toApiDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new API.
     *
     * @param apiUrl the API URL
     * @return created API DTO
     */
    @Override
    public ApiDTO createApi(String apiUrl) {
        String normalizedUrl = normalizeApiUrl(apiUrl);
        if (apiRepository.findByApiUrlIgnoreCase(normalizedUrl).isPresent()) {
            logger.warn("Attempt to create duplicate API: {}", normalizedUrl);
            throw new IllegalArgumentException("API URL already exists");
        }
        ApiEntity entity = new ApiEntity();
        entity.setApiUrl(normalizedUrl);
        ApiEntity saved = apiRepository.save(entity);
        logger.info("API created: id={}, url={}", saved.getId(), saved.getApiUrl());
        return toApiDto(saved);
    }

    /**
     * Updates an existing API.
     *
     * @param apiId  the API ID
     * @param apiUrl the API URL
     * @return updated API DTO
     */
    @Override
    public ApiDTO updateApi(Long apiId, String apiUrl) {
        String normalizedUrl = normalizeApiUrl(apiUrl);
        ApiEntity entity = apiRepository.findById(apiId).orElseThrow();
        if (apiRepository.existsByApiUrlIgnoreCaseAndIdNot(normalizedUrl, apiId)) {
            logger.warn("Attempt to update API to duplicate url: {}", normalizedUrl);
            throw new IllegalArgumentException("API URL already exists");
        }
        entity.setApiUrl(normalizedUrl);
        ApiEntity saved = apiRepository.save(entity);
        logger.info("API updated: id={}, url={}", saved.getId(), saved.getApiUrl());
        return toApiDto(saved);
    }

    /**
     * Deletes APIs by their IDs.
     *
     * @param apiIds list of API IDs
     */
    @Override
    public void deleteApis(List<Long> apiIds) {
        if (apiIds == null || apiIds.isEmpty()) {
            return;
        }
        groupApiRepository.deleteByApiIds(apiIds);
        apiRepository.deleteAllById(apiIds);
        logger.info("APIs deleted: {}", apiIds);
    }

    /**
     * Retrieves user groups for a user.
     *
     * @param userId the user ID
     * @return user groups response
     */
    @Override
    public UserGroupsResponse getUserGroups(Long userId) {
        logger.debug("getUserGroups for userId={}", userId);
        UserGroupsResponse resp = new UserGroupsResponse();
        resp.setUserId(userId);
        List<GroupEntity> allGroups = groupRepository.findAll();
        List<GroupUserEntity> userGroups = groupUserRepository.findByUserId(userId);
        List<Long> assigned = userGroups.stream().map(gu -> gu.getGroup().getId()).collect(Collectors.toList());
        resp.setGroupIds(assigned);
        resp.setGroups(allGroups.stream().map(g -> {
            UserPrivilegesDTO.GroupInfo gi = new UserPrivilegesDTO.GroupInfo();
            gi.setId(g.getId());
            gi.setGroupName(g.getGroupName());
            return gi;
        }).collect(Collectors.toList()));
        return resp;
    }

    /**
     * Updates user groups for a user.
     *
     * @param userId   the user ID
     * @param groupIds list of group IDs
     */
    @Override
    public void updateUserGroups(Long userId, List<Long> groupIds) {
        logger.info("updateUserGroups called for userId={} groups={} ", userId, groupIds);
        // use transaction template with retry to mitigate deadlocks; perform minimal
        // diffs
        int attempts = 3;
        while (true) {
            try {
                txTemplate.execute(status -> {
                    // current groups
                    List<GroupUserEntity> existing = groupUserRepository.findByUserId(userId);
                    List<Long> existingIds = existing.stream().map(g -> g.getGroup().getId())
                            .collect(Collectors.toList());
                    // compute toDelete and toAdd
                    List<Long> toDelete = existingIds.stream().filter(id -> !groupIds.contains(id))
                            .collect(Collectors.toList());
                    List<Long> toAdd = groupIds.stream().filter(id -> !existingIds.contains(id))
                            .collect(Collectors.toList());

                    // targeted deletes
                    toDelete.forEach(gid -> groupUserRepository.deleteByUserIdAndGroupId(userId, gid));

                    // targeted inserts
                    if (!toAdd.isEmpty()) {
                        List<GroupEntity> groups = groupRepository.findAllById(toAdd);
                        List<GroupUserEntity> toSave = groups.stream().map(g -> {
                            GroupUserEntity gu = new GroupUserEntity();
                            gu.setGroup(g);
                            UserEntity u = new UserEntity();
                            u.setId(userId);
                            gu.setUser(u);
                            return gu;
                        }).collect(Collectors.toList());
                        groupUserRepository.saveAll(toSave);
                    }
                    return null;
                });
                logger.info("updateUserGroups success for userId={} after attempt", userId);
                return;
            } catch (CannotAcquireLockException ex) {
                attempts--;
                logger.warn("Deadlock when updating user groups for userId={}, attempts left={}", userId, attempts, ex);
                if (attempts <= 0)
                    throw ex;
                try {
                    Thread.sleep(150L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    /**
     * Retrieves group APIs for a group.
     *
     * @param groupId the group ID
     * @return group APIs response
     */
    @Override
    public GroupApisResponse getGroupApis(Long groupId) {
        logger.debug("getGroupApis for groupId={}", groupId);
        GroupApisResponse resp = new GroupApisResponse();
        resp.setGroupId(groupId);
        List<ApiEntity> allApis = apiRepository.findAll();
        List<GroupApiEntity> gas = groupApiRepository.findByGroupId(groupId);
        List<Long> assigned = gas.stream().map(g -> g.getApi().getId()).collect(Collectors.toList());
        resp.setApiIds(assigned);
        resp.setApis(allApis.stream().map(a -> {
            GroupApisResponse.ApiInfo ai = new GroupApisResponse.ApiInfo();
            ai.setId(a.getId());
            ai.setApiUrl(a.getApiUrl());
            return ai;
        }).collect(Collectors.toList()));
        return resp;
    }

    /**
     * Updates group APIs for a group.
     *
     * @param groupId the group ID
     * @param apiIds  list of API IDs
     */
    @Override
    public void updateGroupApis(Long groupId, List<Long> apiIds) {
        logger.info("updateGroupApis called for groupId={} apis={} ", groupId, apiIds);
        int attempts = 3;
        while (true) {
            try {
                txTemplate.execute(status -> {
                    List<GroupApiEntity> existing = groupApiRepository.findByGroupId(groupId);
                    List<Long> existingApiIds = existing.stream().map(x -> x.getApi().getId()).distinct().toList();

                    List<Long> requestedApiIds = apiIds == null ? List.of()
                            : apiIds.stream()
                                    .filter(id -> id != null && id > 0)
                                    .distinct()
                                    .toList();

                    List<Long> toDelete = existingApiIds.stream()
                            .filter(id -> !requestedApiIds.contains(id))
                            .toList();
                    List<Long> toAdd = requestedApiIds.stream()
                            .filter(id -> !existingApiIds.contains(id))
                            .toList();

                    toDelete.forEach(apiId -> groupApiRepository.deleteByGroupIdAndApiId(groupId, apiId));

                    if (!toAdd.isEmpty()) {
                        List<ApiEntity> apis = apiRepository.findAllById(toAdd);
                        GroupEntity g = groupRepository.findById(groupId).orElseThrow();
                        List<GroupApiEntity> toSave = apis.stream().map(a -> {
                            GroupApiEntity ga = new GroupApiEntity();
                            ga.setGroup(g);
                            ga.setApi(a);
                            return ga;
                        }).collect(Collectors.toList());
                        groupApiRepository.saveAll(toSave);
                    }
                    return null;
                });
                logger.info("updateGroupApis success for groupId={} after attempt", groupId);
                return;
            } catch (CannotAcquireLockException ex) {
                attempts--;
                logger.warn("Deadlock when updating group apis for groupId={}, attempts left={}", groupId, attempts,
                        ex);
                if (attempts <= 0)
                    throw ex;
                try {
                    Thread.sleep(150L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    private GroupPrivilegesDTO toGroupPrivilegesDto(GroupEntity group, List<String> apiUrls) {
        GroupPrivilegesDTO dto = new GroupPrivilegesDTO();
        dto.setId(group.getId());
        dto.setGroupName(group.getGroupName());
        dto.setDisplayName(resolveGroupDisplayName(group.getGroupName()));
        dto.setApis(apiUrls);
        return dto;
    }

    private ApiDTO toApiDto(ApiEntity api) {
        ApiDTO dto = new ApiDTO();
        dto.setId(api.getId());
        dto.setApiUrl(api.getApiUrl());
        return dto;
    }

    private String resolveGroupDisplayName(String groupName) {
        String display = null;
        try {
            display = messageSource.getMessage(groupName, null, LocaleContextHolder.getLocale());
        } catch (Exception ignored) {
        }
        if (display == null || display.trim().isEmpty()) {
            try {
                display = messageSource.getMessage("group." + groupName.toLowerCase(), null,
                        LocaleContextHolder.getLocale());
            } catch (Exception ignored) {
                display = groupName;
            }
        }
        return display;
    }

    private String normalizeGroupName(String groupName) {
        if (!StringUtils.hasText(groupName)) {
            throw new IllegalArgumentException("Group name is required");
        }
        return groupName.trim();
    }

    private String normalizeApiUrl(String apiUrl) {
        if (!StringUtils.hasText(apiUrl)) {
            throw new IllegalArgumentException("API URL is required");
        }
        return apiUrl.trim();
    }

    // Utility and business logic methods moved from AdminPrivilegesController


    @Override
    public boolean isSameGroupName(Long groupId, String groupName) {
        if (groupId == null || groupName == null || groupName.trim().isEmpty()) {
            return false;
        }
        Optional<String> existingOpt = groupRepository.findGroupNameById(groupId);
        return existingOpt.map(existing -> existing.trim().equalsIgnoreCase(groupName.trim())).orElse(false);
    }


    @Override
    public boolean isSameApiUrl(Long apiId, String apiUrl) {
        if (apiId == null || apiUrl == null || apiUrl.trim().isEmpty()) {
            return false;
        }
        Optional<String> existingOpt = apiRepository.findApiUrlById(apiId);
        return existingOpt.map(existing -> existing.trim().equalsIgnoreCase(apiUrl.trim())).orElse(false);
    }

    @Override
    public boolean isSameUserGroupAssignment(Long userId, List<Long> groupIds) {
        if (userId == null) {
            return false;
        }
        List<Long> current = uniquePositiveIds(getUserGroups(userId).getGroupIds());
        List<Long> requested = groupIds == null ? List.of() : uniquePositiveIds(groupIds);
        return new LinkedHashSet<>(current).equals(new LinkedHashSet<>(requested));
    }

    @Override
    public boolean isSameGroupApiAssignment(Long groupId, List<Long> apiIds) {
        if (groupId == null) {
            return false;
        }
        List<Long> current = uniquePositiveIds(getGroupApis(groupId).getApiIds());
        List<Long> requested = apiIds == null ? List.of() : uniquePositiveIds(apiIds);
        return new LinkedHashSet<>(current).equals(new LinkedHashSet<>(requested));
    }

    @Override
    public UserPrivilegesDTO mapToUserPrivilegesDTO(com.example.bookportal.entity.UserEntity user) {
        if (user == null) {
            return null;
        }
        UserPrivilegesDTO dto = new UserPrivilegesDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        UserGroupsResponse userGroups = getUserGroups(user.getId());
        LinkedHashSet<Long> assignedGroupIds = new LinkedHashSet<>(uniquePositiveIds(userGroups.getGroupIds()));
        List<UserPrivilegesDTO.GroupInfo> assignedGroups = userGroups.getGroups() == null
                ? List.of()
                : userGroups.getGroups().stream()
                        .filter(group -> group.getId() != null && assignedGroupIds.contains(group.getId()))
                        .collect(Collectors.toList());
        dto.setGroups(assignedGroups);
        return dto;
    }

    @Override
    public String msg(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    @Override
    public Long resolveDefaultGroupId() {
        Optional<GroupEntity> adminGroup = groupRepository.findByGroupNameIgnoreCase("admin");
        if (adminGroup.isPresent()) {
            return adminGroup.get().getId();
        }
        return groupRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(GroupEntity::getId)
                .findFirst()
                .orElse(null);
    }

    // Helper for unique positive IDs (copied from BaseController)
    private List<Long> uniquePositiveIds(List<Long> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Long> unique = new LinkedHashSet<>();
        for (Long id : rawIds) {
            if (id != null && id > 0) {
                unique.add(id);
            }
        }
        return new ArrayList<>(unique);
    }

    private Comparator<ApiDTO> buildApiComparator(Pageable pageable) {
        Sort sort = pageable == null ? Sort.unsorted() : pageable.getSort();
        Comparator<ApiDTO> comparator;
        if (sort != null && sort.isSorted()) {
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            boolean asc = order.isAscending();
            comparator = switch (property) {
                case "id" -> Comparator.comparing(ApiDTO::getId, Comparator.nullsLast(Long::compareTo));
                case "apiUrl" -> Comparator.comparing(ApiDTO::getApiUrl, Comparator.nullsLast(String::compareToIgnoreCase));
                default -> Comparator.comparing(ApiDTO::getApiUrl, Comparator.nullsLast(String::compareToIgnoreCase));
            };
            if (!asc) {
                comparator = comparator.reversed();
            }
            return comparator;
        }
        return Comparator.comparing(ApiDTO::getApiUrl, Comparator.nullsLast(String::compareToIgnoreCase));
    }
}
