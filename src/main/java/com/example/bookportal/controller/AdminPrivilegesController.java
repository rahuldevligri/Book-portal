package com.example.bookportal.controller;

import com.example.bookportal.dto.*;
import com.example.bookportal.service.PrivilegesService;
import com.example.bookportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import jakarta.persistence.EntityNotFoundException;

/**
 * Controller for admin privileges management operations.
 * <p>
 * Handles CRUD operations for user groups, API manager, and privilege
 * assignments.
 * Provides endpoints for admin privileges panel.
 */
@Controller
@RequestMapping("/admin/privileges")
public class AdminPrivilegesController extends BaseController {
    // Pagination and limit constants
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int DEFAULT_PAGE_WINDOW = 5;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int ASSIGNMENT_FETCH_SIZE = 5;
    private static final Set<String> GROUP_SORT_FIELDS = Set.of("id", "groupName");
    private static final Set<String> USER_SORT_FIELDS = Set.of("id", "firstName", "lastName", "email", "userName");
    private static final Set<String> API_SORT_FIELDS = Set.of("id", "apiUrl");
    
    // Tab constants
    private static final String TAB_USER_GROUP = "user-group";
    private static final String TAB_API_MANAGER = "api-manager";
    private static final String REDIRECT_USER_GROUP = "redirect:/admin/privileges?tab=user-group";
    private static final String REDIRECT_API_MANAGER = "redirect:/admin/privileges?tab=api-manager";

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private UserService userService;

    /**
     * Displays the admin privileges management page with tabs for user groups and
     * API manager.
     *
     * @param tab             selected tab
     * @param selectedGroupId selected group ID
     * @param usersPage       user page number
     * @param groupsPage      group page number
     * @param apisPage        API page number
     * @param size            page size
     * @param groupsSort      group sort field
     * @param groupsDirection group sort direction
     * @param usersSort       user sort field
     * @param usersDirection  user sort direction
     * @param apisSort        API sort field
     * @param apisDirection   API sort direction
     * @param editApisGroupId edit API group ID
     * @param groupId         group ID
     * @param modal           modal type
     * @param modalGroupId    modal group ID
     * @param modalApiId      modal API ID
     * @param modalUserId     modal user ID
     * @param model           model to populate view attributes
     * @return view name for admin privileges page
     */
    @GetMapping
    public String view(@RequestParam(defaultValue = "user-group") String tab,
            @RequestParam(required = false) Long selectedGroupId,
            @RequestParam(defaultValue = "0") int usersPage,
            @RequestParam(defaultValue = "0") int groupsPage,
            @RequestParam(defaultValue = "0") int apisPage,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "groupName") String groupsSort,
            @RequestParam(defaultValue = "asc") String groupsDirection,
            @RequestParam(defaultValue = "firstName") String usersSort,
            @RequestParam(defaultValue = "asc") String usersDirection,
            @RequestParam(defaultValue = "apiUrl") String apisSort,
            @RequestParam(defaultValue = "asc") String apisDirection,
            @RequestParam(required = false) Long editApisGroupId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) String modal,
            @RequestParam(required = false) Long modalGroupId,
            @RequestParam(required = false) Long modalApiId,
            @RequestParam(required = false) Long modalUserId,
            @RequestParam(defaultValue = "0") int userComboPage,
            @RequestParam(defaultValue = "5") int userComboSize,
            Model model) {
        int safeUsersPage = safePage(usersPage);
        int safeGroupsPage = safePage(groupsPage);
        int safeApisPage = safePage(apisPage);
        int safeSize = boundedPageSize(size, DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
        String safeGroupsSort = sanitizeSort(groupsSort, "groupName", GROUP_SORT_FIELDS);
        String safeGroupsDirection = sanitizeDirection(groupsDirection);
        String safeUsersSort = sanitizeSort(usersSort, "firstName", USER_SORT_FIELDS);
        String safeUsersDirection = sanitizeDirection(usersDirection);
        String safeApisSort = sanitizeSort(apisSort, "apiUrl", API_SORT_FIELDS);
        String safeApisDirection = sanitizeDirection(apisDirection);
        String safeTab = TAB_API_MANAGER.equals(tab) ? TAB_API_MANAGER : TAB_USER_GROUP;

        Pageable groupsPageable = pageable(safeGroupsPage, safeSize, safeGroupsSort, safeGroupsDirection, DEFAULT_PAGE_WINDOW, MAX_PAGE_SIZE, "groupName");
        Page<GroupPrivilegesDTO> groupPrivilegesResult = privilegesService.listGroups("", groupsPageable);

        boolean showEditGroupApisModal = "edit-group-apis".equals(modal);
        boolean needsAllUsers = "edit-user-groups".equals(modal);
        boolean needsModalUser = modalUserId != null;
        boolean needsAllGroups = showEditGroupApisModal || needsAllUsers;

        List<GroupPrivilegesDTO> allGroupsForAssignment = List.of();
        if (needsAllGroups) {
            allGroupsForAssignment = privilegesService.listAllGroupsForManagement();
        }

        Long resolvedGroupId = selectedGroupId;
        if (resolvedGroupId == null) {
            resolvedGroupId = privilegesService.resolveDefaultGroupId();
        }

        boolean needsUsersTable = TAB_USER_GROUP.equals(safeTab);
        boolean needsApisTable = TAB_API_MANAGER.equals(safeTab) || "edit-api".equals(modal);

        Pageable usersPageable = pageable(safeUsersPage, safeSize, safeUsersSort, safeUsersDirection, DEFAULT_PAGE_WINDOW, MAX_PAGE_SIZE, "firstName");
        Page<UserPrivilegesDTO> usersResult = Page.empty(usersPageable);
        if (needsUsersTable && resolvedGroupId != null) {
            usersResult = privilegesService.listUsers("", resolvedGroupId, usersPageable);
        }

        Pageable apisPageable = pageable(safeApisPage, safeSize, safeApisSort, safeApisDirection, DEFAULT_PAGE_WINDOW, MAX_PAGE_SIZE, "apiUrl");
        Page<ApiDTO> apisResult = Page.empty(apisPageable);
        if (needsApisTable && resolvedGroupId != null) {
            apisResult = privilegesService.listApisForGroup(resolvedGroupId, apisPageable);
        }

        GroupApisResponse selectedGroupApis = null;
        Long resolvedEditApisGroupId = editApisGroupId != null ? editApisGroupId : groupId;
        if (resolvedEditApisGroupId == null) {
            resolvedEditApisGroupId = resolvedGroupId;
        }

        Page<UserPrivilegesDTO> allUsersForAssignmentPage = Page.empty(PageRequest.of(0, ASSIGNMENT_FETCH_SIZE));
        if (needsAllUsers) {
            Pageable comboPageable = PageRequest.of(userComboPage, userComboSize, Sort.by(Sort.Direction.ASC, "firstName"));
            allUsersForAssignmentPage = privilegesService.listUsers("", null, comboPageable);
        }

        List<ApiDTO> allApisForAssignment = showEditGroupApisModal
                ? privilegesService.listAllApisForAssignment()
                : List.of();

        if (showEditGroupApisModal && resolvedEditApisGroupId != null) {
            selectedGroupApis = privilegesService.getGroupApis(resolvedEditApisGroupId);
        }

        model.addAttribute("tab", safeTab);
        model.addAttribute("selectedGroupId", resolvedGroupId);
        model.addAttribute("users", usersResult.getContent());
        model.addAttribute("usersPage", usersResult.getNumber());
        model.addAttribute("usersTotalPages", usersResult.getTotalPages());
        model.addAttribute("groupPrivileges", groupPrivilegesResult.getContent());
        model.addAttribute("groupsPage", groupPrivilegesResult.getNumber());
        model.addAttribute("groupsTotalPages", groupPrivilegesResult.getTotalPages());
        model.addAttribute("apiManagement", apisResult.getContent());
        model.addAttribute("apiPage", apisResult.getNumber());
        model.addAttribute("apiTotalPages", apisResult.getTotalPages());
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("groupsSort", safeGroupsSort);
        model.addAttribute("groupsDirection", safeGroupsDirection);
        model.addAttribute("usersSort", safeUsersSort);
        model.addAttribute("usersDirection", safeUsersDirection);
        model.addAttribute("apisSort", safeApisSort);
        model.addAttribute("apisDirection", safeApisDirection);
        model.addAttribute("allUsers", allUsersForAssignmentPage.getContent());
        model.addAttribute("userComboPage", allUsersForAssignmentPage.getNumber());
        model.addAttribute("userComboTotalPages", allUsersForAssignmentPage.getTotalPages());
        model.addAttribute("userComboSize", allUsersForAssignmentPage.getSize());
        model.addAttribute("allGroups", allGroupsForAssignment);
        model.addAttribute("allApis", allApisForAssignment);
        model.addAttribute("editApisGroupId", resolvedEditApisGroupId);
        model.addAttribute("editApisSelection", selectedGroupApis);

        GroupPrivilegesDTO modalGroup = null;
        if (modalGroupId != null) {
            List<GroupPrivilegesDTO> modalGroupSource = allGroupsForAssignment.isEmpty()
                    ? groupPrivilegesResult.getContent()
                    : allGroupsForAssignment;
            modalGroup = modalGroupSource.stream()
                    .filter(group -> Objects.equals(group.getId(), modalGroupId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Group not found: " + modalGroupId));
        }

        ApiDTO modalApi = null;
        if (modalApiId != null) {
            modalApi = apisResult.getContent().stream()
                .filter(api -> Objects.equals(api.getId(), modalApiId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("API not found: " + modalApiId));
        }

        UserPrivilegesDTO modalUser = null;
        if (needsModalUser) {
            // Use the correct user list for lookup: allUsersForAssignmentPage.getContent() (paginated) or fallback to userService
            List<UserPrivilegesDTO> userListForModal = allUsersForAssignmentPage.getContent();
            modalUser = userListForModal.stream()
                .filter(user -> Objects.equals(user.getId(), modalUserId))
                .findFirst()
                .orElseGet(() -> {
                    var userEntity = userService.findById(modalUserId);
                    if (userEntity == null) {
                        throw new EntityNotFoundException("User not found: " + modalUserId);
                    }
                    return privilegesService.mapToUserPrivilegesDTO(userEntity);
                });
        }

        model.addAttribute("modal", modal);
        model.addAttribute("modalUserId", modalUserId);
        model.addAttribute("modalGroup", modalGroup);
        model.addAttribute("modalApi", modalApi);
        model.addAttribute("modalUser", modalUser);
        return "user-privileges";
    }

        /**
         * Handles creation of a new user group.
         *
         * @param groupName name of the group to create
         * @param model model to populate view attributes
         * @param tab selected tab
         * @param selectedGroupId selected group ID
         * @param groupsPage group page number
         * @param size page size
         * @param groupsSort group sort field
         * @param groupsDirection group sort direction
         * @return redirect or form view
         */
        @PostMapping("/group-management/add")
        public String createGroupForm(@RequestParam String groupName,
            Model model,
            @RequestParam(defaultValue = "user-group") String tab,
            @RequestParam(required = false) Long selectedGroupId,
            @RequestParam(defaultValue = "0") int groupsPage,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "groupName") String groupsSort,
            @RequestParam(defaultValue = "asc") String groupsDirection,
            RedirectAttributes redirectAttributes) {
        String trimmed = groupName == null ? "" : groupName.trim();
        if (trimmed.isEmpty()) {
            model.addAttribute("modal", "add-group");
            model.addAttribute("modalGroupError", privilegesService.msg("NotBlank.name"));
            // Re-populate modal context
            Pageable groupsPageable = pageable(groupsPage, size, groupsSort, groupsDirection, 5, 100, "groupName");
            Page<GroupPrivilegesDTO> groupPrivilegesResult = privilegesService.listGroups("", groupsPageable);
            model.addAttribute("groupPrivileges", groupPrivilegesResult.getContent());
            model.addAttribute("groupsPage", groupPrivilegesResult.getNumber());
            model.addAttribute("groupsTotalPages", groupPrivilegesResult.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("groupsSort", groupsSort);
            model.addAttribute("groupsDirection", groupsDirection);
            model.addAttribute("tab", tab);
            model.addAttribute("selectedGroupId", selectedGroupId);
            return "user-privileges";
        }
        try {
            privilegesService.createGroup(trimmed);
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.add.success"));
        } catch (Exception e) {
            model.addAttribute("modal", "add-group");
            model.addAttribute("modalGroupError", e.getMessage());
            Pageable groupsPageable = pageable(groupsPage, size, groupsSort, groupsDirection, 5, 100, "groupName");
            Page<GroupPrivilegesDTO> groupPrivilegesResult = privilegesService.listGroups("", groupsPageable);
            model.addAttribute("groupPrivileges", groupPrivilegesResult.getContent());
            model.addAttribute("groupsPage", groupPrivilegesResult.getNumber());
            model.addAttribute("groupsTotalPages", groupPrivilegesResult.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("groupsSort", groupsSort);
            model.addAttribute("groupsDirection", groupsDirection);
            model.addAttribute("tab", tab);
            model.addAttribute("selectedGroupId", selectedGroupId);
            return "user-privileges";
        }
        return REDIRECT_USER_GROUP;
    }

        /**
         * Handles updating an existing user group.
         *
         * @param groupId ID of the group to update
         * @param groupName new group name
         * @param selectedGroupId selected group ID
         * @param groupsPage group page number
         * @param size page size
         * @param groupsSort group sort field
         * @param groupsDirection group sort direction
         * @param model model to populate view attributes
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect or form view
         */
        @PostMapping("/group-management/update")
        public String updateGroupForm(@RequestParam Long groupId,
            @RequestParam String groupName,
            @RequestParam(required = false) Long selectedGroupId,
            @RequestParam(defaultValue = "0") int groupsPage,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "groupName") String groupsSort,
            @RequestParam(defaultValue = "asc") String groupsDirection,
            Model model,
            RedirectAttributes redirectAttributes) {
        String trimmed = groupName == null ? "" : groupName.trim();
        if (trimmed.isEmpty()) {
            Pageable groupsPageable = pageable(groupsPage, size, groupsSort, groupsDirection, 5, 100, "groupName");
            Page<GroupPrivilegesDTO> groupPrivilegesResult = privilegesService.listGroups("", groupsPageable);
            GroupPrivilegesDTO modalGroup = groupPrivilegesResult.getContent().stream()
                    .filter(g -> Objects.equals(g.getId(), groupId))
                    .findFirst()
                    .orElse(null);
            Pageable usersPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "firstName"));
            Page<UserPrivilegesDTO> userPrivilegesResult = privilegesService.listUsers("", selectedGroupId, usersPageable);
            model.addAttribute("tab", "user-group");
            model.addAttribute("selectedGroupId", selectedGroupId);
            model.addAttribute("groupPrivileges", groupPrivilegesResult.getContent());
            model.addAttribute("users", userPrivilegesResult.getContent());
            model.addAttribute("groupsPage", groupPrivilegesResult.getNumber());
            model.addAttribute("groupsTotalPages", groupPrivilegesResult.getTotalPages());
            model.addAttribute("usersPage", userPrivilegesResult.getNumber());
            model.addAttribute("usersTotalPages", userPrivilegesResult.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("groupsSort", groupsSort);
            model.addAttribute("groupsDirection", groupsDirection);
            model.addAttribute("modal", "edit-group");
            model.addAttribute("modalGroup", modalGroup);
            model.addAttribute("modalGroupError", privilegesService.msg("NotBlank.name"));
            return "user-privileges";
        }
        if (privilegesService.isSameGroupName(groupId, trimmed)) {
            redirectAttributes.addFlashAttribute("info", privilegesService.msg("admin.action.nochange"));
            return REDIRECT_USER_GROUP;
        }
        try {
            privilegesService.updateGroup(groupId, trimmed);
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.edit.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_USER_GROUP;
    }

        /**
         * Handles deletion of user groups.
         *
         * @param groupIds list of group IDs to delete
         * @param confirm confirmation flag
         * @param model model to populate view attributes
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect or confirmation view
         */
        @PostMapping("/group-management/delete")
        public String deleteGroupsForm(@RequestParam(name = "groupIds", required = false) List<Long> groupIds,
            @RequestParam(defaultValue = "false") boolean confirm,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(groupIds);
        if (safeIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", privilegesService.msg("admin.action.delete.noselection"));
            return REDIRECT_USER_GROUP;
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "confirm.delete.groups",
                    "/admin/privileges/group-management/delete",
                    userGroupPath(null),
                    hiddenParamsForIds("groupIds", safeIds, null));
        }
        try {
            privilegesService.deleteGroups(privilegesService.sanitizePositiveIds(safeIds, "groupIds", true));
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_USER_GROUP;
    }

        /**
         * Handles deletion of users from a group.
         *
         * @param userIds list of user IDs to delete
         * @param selectedGroupId selected group ID
         * @param confirm confirmation flag
         * @param model model to populate view attributes
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect or confirmation view
         */
        @PostMapping("/users/delete")
        public String deleteUsersForm(@RequestParam(name = "userIds", required = false) List<Long> userIds,
            @RequestParam(required = false) Long selectedGroupId,
            @RequestParam(defaultValue = "false") boolean confirm,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(userIds);
        String redirect = userGroupRedirect(selectedGroupId);
        if (safeIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", privilegesService.msg("admin.action.delete.noselection"));
            return redirect;
        }
        if (!confirm) {
            Map<String, String> extra = new LinkedHashMap<>();
            if (selectedGroupId != null) {
                extra.put("selectedGroupId", String.valueOf(selectedGroupId));
            }
            return showDeleteConfirmation(
                    model,
                    "delete.user.confirmation",
                    "/admin/privileges/users/delete",
                    userGroupPath(selectedGroupId),
                    hiddenParamsForIds("userIds", safeIds, extra));
        }
        try {
            userService.deleteUsersByIds(privilegesService.sanitizePositiveIds(safeIds, "userIds", true));
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirect;
    }

        /**
         * Handles updating user group assignments.
         *
         * @param userId user ID
         * @param groupIds list of group IDs to assign
         * @param model model to populate view attributes
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect or form view
         */
        @PostMapping("/users/groups/update")
        public String updateUserGroupsForm(@RequestParam(required = false) Long userId,
            @RequestParam(name = "groupIds", required = false) List<Long> groupIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (userId == null) {
            // Re-populate modal data for validation error display
            List<GroupPrivilegesDTO> allGroupsForAssignment = privilegesService.listAllGroupsForManagement();
            List<UserPrivilegesDTO> allUsersForAssignment = privilegesService
                    .listUsers("", null, PageRequest.of(0, 200)).getContent();

            model.addAttribute("modal", "edit-user-groups");
            model.addAttribute("allUsers", allUsersForAssignment);
            model.addAttribute("allGroups", allGroupsForAssignment);
            model.addAttribute("userSelectionError", privilegesService.msg("NotNull.userId"));
            return "user-privileges";
        }
        List<Long> safeGroupIds = privilegesService.sanitizePositiveIdsForGroups(groupIds);
        if (privilegesService.isSameUserGroupAssignment(userId, safeGroupIds)) {
            redirectAttributes.addFlashAttribute("info", privilegesService.msg("admin.action.nochange"));
            return REDIRECT_USER_GROUP;
        }
        try {
            privilegesService.updateUserGroups(userId, safeGroupIds);
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.edit.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_USER_GROUP;
    }

        /**
         * Handles updating an API URL.
         *
         * @param apiId API ID
         * @param apiUrl new API URL
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect view
         */
        @PostMapping("/api-management/update")
        public String updateApiForm(@RequestParam Long apiId,
            @RequestParam String apiUrl,
            RedirectAttributes redirectAttributes) {
        if (privilegesService.isSameApiUrl(apiId, apiUrl)) {
            redirectAttributes.addFlashAttribute("info", privilegesService.msg("admin.action.nochange"));
            return REDIRECT_API_MANAGER;
        }
        try {
            privilegesService.updateApi(apiId, apiUrl.trim());
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.edit.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_API_MANAGER;
    }

        /**
         * Handles deletion of APIs.
         *
         * @param apiIds list of API IDs to delete
         * @param confirm confirmation flag
         * @param model model to populate view attributes
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect or confirmation view
         */
        @PostMapping("/api-management/delete")
        public String deleteApisForm(@RequestParam(name = "apiIds", required = false) List<Long> apiIds,
            @RequestParam(defaultValue = "false") boolean confirm,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(apiIds);
        if (safeIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", privilegesService.msg("admin.action.delete.noselection"));
            return REDIRECT_API_MANAGER;
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "confirm.delete.apis",
                    "/admin/privileges/api-management/delete",
                    "/admin/privileges?tab=api-manager",
                    hiddenParamsForIds("apiIds", safeIds, null));
        }
        try {
            privilegesService.deleteApis(privilegesService.sanitizePositiveIds(safeIds, "apiIds", true));
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_API_MANAGER;
    }

        /**
         * Handles updating API assignments for a group.
         *
         * @param groupId group ID
         * @param apiIds list of API IDs to assign
         * @param redirectAttributes redirect attributes for feedback
         * @return redirect view
         */
        @PostMapping("/groups/apis/update")
        public String updateGroupApisForm(@RequestParam Long groupId,
            @RequestParam(name = "apiIds", required = false) List<Long> apiIds,
            RedirectAttributes redirectAttributes) {
        if (privilegesService.isSameGroupApiAssignment(groupId, apiIds)) {
            redirectAttributes.addFlashAttribute("info", privilegesService.msg("admin.action.nochange"));
            return apiManagerEditApisRedirect(groupId);
        }
        List<Long> safeApiIds = apiIds == null ? List.of() : privilegesService.sanitizePositiveIds(apiIds, "apiIds", false);
        try {
            privilegesService.updateGroupApis(groupId, safeApiIds);
            redirectAttributes.addFlashAttribute("success", privilegesService.msg("admin.action.edit.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return apiManagerEditApisRedirect(groupId);
    }

    private String userGroupRedirect(Long selectedGroupId) {
        return "redirect:" + userGroupPath(selectedGroupId);
    }

    private String userGroupPath(Long selectedGroupId) {
        return "/admin/privileges?tab=user-group" + withGroupParam(selectedGroupId);
    }

    private String apiManagerEditApisRedirect(Long groupId) {
        return REDIRECT_API_MANAGER + "&editApisGroupId=" + groupId;
    }

    private String withGroupParam(Long selectedGroupId) {
        if (selectedGroupId == null) {
            return "";
        }
        return "&selectedGroupId=" + selectedGroupId;
    }

    private String sanitizeSort(String sort, String defaultSort, Set<String> allowed) {
        if (sort == null || sort.isBlank()) {
            return defaultSort;
        }
        String requested = sort.trim();
        return allowed.contains(requested) ? requested : defaultSort;
    }

    private String sanitizeDirection(String direction) {
        return "desc".equalsIgnoreCase(direction) ? "desc" : "asc";
    }

    // ID sanitization logic moved to PrivilegesService.

    // All business logic and utility methods have been moved to PrivilegesService and UserService.
    // The controller now delegates to the service layer for all such operations.
}
