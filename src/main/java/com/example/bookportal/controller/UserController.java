package com.example.bookportal.controller;

import com.example.bookportal.dto.AdminEditUserFormDTO;
import com.example.bookportal.dto.ChangePasswordFormDTO;
import com.example.bookportal.dto.EditProfileFormDTO;
import com.example.bookportal.dto.RegisterFormDTO;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.service.AuthorService;
import com.example.bookportal.service.PublisherService;
import com.example.bookportal.service.SecretQuestionService;
import com.example.bookportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import com.example.bookportal.service.UserTypeService;
import java.util.Map;
import java.util.Set;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Controller for user profile and admin user management operations.
 * <p>
 * Handles user profile editing, password changes, and admin user CRUD
 * operations.
 */
@Controller
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final Set<String> ADMIN_USER_SORT_FIELDS = Set.of(
            "id", "firstName", "lastName", "email", "userName", "secretQuestionId", "secretAnswer", "userTypeId");
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private UserService userService;
    @Autowired
    private SecretQuestionService secretQuestionService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Displays the admin edit user page for a specific user.
     * 
     * @param model model to populate view attributes
     * @param id    user ID
     * @return view name for admin edit user page
     */
    @GetMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Long id,
            @RequestParam(value = "returnTo", required = false) String returnTo,
            Model model) {
        AdminEditUserFormDTO form = userService.buildAdminEditForm(id);
        return showAdminEditUserForm(model, form, null, returnTo);
    }

    /**
     * Handles the submission of the admin edit user form.
     *
     * @param id                 ID of the user to update
     * @param form               AdminEditUserFormDTO containing user data
     * @param redirectAttributes Redirect attributes for feedback messages
     * @return Redirect or view name based on outcome
     */
    @PostMapping("/admin/users/edit/{id}")
    public String updateUserFromAdmin(@PathVariable Long id,
            @Valid @ModelAttribute("user") AdminEditUserFormDTO form,
            BindingResult bindingResult,
            @RequestParam(value = "returnTo", required = false) String returnTo,
            Model model,
            RedirectAttributes redirectAttributes) {
        form.setId(id);

        if (userService.isSecretAnswerRequiredForAdminEdit(id, form.getSecretQuestionId(), form.getSecretAnswer())) {
            bindingResult.rejectValue("secretAnswer", "NotBlank.secretAnswer");
        }

        if (bindingResult.hasErrors()) {
            return showAdminEditUserForm(model, form, null, returnTo);
        }
        try {
            boolean changed = userService.updateUserFromAdminForm(form);
            if (!changed) {
                redirectAttributes.addFlashAttribute("info",
                        messageSource.getMessage("admin.action.nochange", null, LocaleContextHolder.getLocale()));
            } else {
                redirectAttributes.addFlashAttribute("success",
                        messageSource.getMessage("admin.action.edit.success", null, LocaleContextHolder.getLocale()));
            }
            if ("privileges".equalsIgnoreCase(returnTo)) {
                return "redirect:/admin/privileges";
            }
            return "redirect:/admin/users";
        } catch (Exception ex) {
            return showAdminEditUserForm(model, form, ex.getMessage(), returnTo);
        }
    }

    /**
     * Displays the advanced search page.
     *
     * @return View name for advanced search page
     */
    @GetMapping("/advanced-search")
    public String advancedSearchPage() {
        return "advanced-search";
    }

    @GetMapping("/search/quick")
    public String quickSearch(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "type", required = false, defaultValue = "Title") String type,
            @RequestParam(name = "matchType", required = false) String matchType,
            Model model) {
        String safeQuery = query == null ? "" : query.trim();
        if (safeQuery.length() < 2) {
            model.addAttribute("searchValidationError", true);
            model.addAttribute("query", safeQuery);
            model.addAttribute("type", type == null ? "Title" : type);
            model.addAttribute("matchType", matchType == null ? "contains" : matchType);
            return "dashboard";
        }
        return buildSearchRedirect(type, safeQuery, matchType);
    }

    @GetMapping("/search/author")
    public String searchAuthorRedirect(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "matchType", required = false) String matchType) {
        return buildSearchRedirect("Author", query, matchType);
    }

    @GetMapping("/search/publisher")
    public String searchPublisherRedirect(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "matchType", required = false) String matchType) {
        return buildSearchRedirect("Publisher", query, matchType);
    }

    @GetMapping("/search/title")
    public String searchTitleRedirect(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "matchType", required = false) String matchType) {
        return buildSearchRedirect("Title", query, matchType);
    }

    /**
     * Displays the user dashboard page.
     *
     * @return View name for dashboard page
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        logger.info("Dashboard accessed by user: {}", userService.resolveCurrentUsername(auth));
        return "dashboard";
    }

    /**
     * Displays the change password page.
     *
     * @return View name for change password page
     */
    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        logger.info("Change password page accessed");
        model.addAttribute("changePasswordForm", new ChangePasswordFormDTO());
        return "change-password";
    }

    /**
     * Handles the submission of the change password form.
     *
     * @param form ChangePasswordFormDTO containing password data
     * @return Redirect or view name based on outcome
     */
    @PostMapping("/change-password")
    public String changePassword(
            Authentication auth,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordFormDTO form,
            BindingResult bindingResult,
            Model model) {
        String username = userService.resolveCurrentUsername(auth);
        logger.info("Password change attempt for user: {}", username);
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordForm", form);
            return "change-password";
        }
        try {
            userService.changePassword(username, form);
            logger.info("Password changed for user: {}", username);
            model.addAttribute("changePasswordForm", new ChangePasswordFormDTO());
            model.addAttribute("success",
                    messageSource.getMessage("password.changed", null, LocaleContextHolder.getLocale()));
            return "change-password";
        } catch (ValidationException ex) {
            logger.error("Password change failed: {}", ex.getMessage(), ex);
            model.addAttribute("changePasswordForm", form);
            if (ex.getMessage().equals(messageSource.getMessage("old.password.invalid", null,
                    LocaleContextHolder.getLocale()))) {
                bindingResult.rejectValue("oldPassword", "old.password.invalid", ex.getMessage());
            } else {
                model.addAttribute("error", ex.getMessage());
            }
            return "change-password";
        } catch (Exception ex) {
            logger.error("Password change failed: {}", ex.getMessage(), ex);
            model.addAttribute("changePasswordForm", form);
            model.addAttribute("error", ex.getMessage());
            return "change-password";
        }
    }

    /**
     * Displays the user options page.
     *
     * @return View name for user options page
     */
    @GetMapping("/user-options")
    public String userOptions(Authentication auth) {
        logger.info("UserEntity options accessed by user: {}", userService.resolveCurrentUsername(auth));
        return "user-options";
    }

    /**
     * Displays the edit profile page for the current user.
     *
     * @param authentication Authentication object for current user
     * @return View name for edit profile page
     */
    @GetMapping("/edit-profile")
    public String editProfile(Model model, Authentication authentication) {
        String username = userService.resolveCurrentUsername(authentication);
        logger.info("Edit profile page accessed by user: {}", username);
        EditProfileFormDTO form = userService.buildEditProfileForm(username);
        model.addAttribute("editProfileForm", form);
        addSecretQuestions(model);
        return "edit-profile";
    }

    /**
     * Handles the submission of the edit profile form.
     *
     * @param form               EditProfileFormDTO containing profile data
     * @param model              Model to populate view attributes
     * @param redirectAttributes Redirect attributes for feedback messages
     * @return Redirect or view name based on outcome
     */
    @PostMapping("/edit-profile")
    public String updateProfile(
            @Valid @ModelAttribute("editProfileForm") EditProfileFormDTO form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("Profile update attempt for user: {}", form.getUsername());
        if (userService.isSecretAnswerRequiredForProfileEdit(
                form.getUsername(),
                form.getSecretQuestionId(),
                form.getSecretAnswer())) {
            bindingResult.rejectValue("secretAnswer", "NotBlank.secretAnswer");
        }
        if (bindingResult.hasErrors()) {
            addSecretQuestions(model);
            return "edit-profile";
        }
        try {
            boolean updated = userService.updateProfileFromForm(form);
            logger.info("Profile updated for user: {}", form.getUsername());
            if (updated) {
                redirectAttributes.addFlashAttribute("success",
                        messageSource.getMessage("profile.updated.success", null, LocaleContextHolder.getLocale()));
            } else {
                redirectAttributes.addFlashAttribute("info",
                        messageSource.getMessage("admin.action.nochange", null, LocaleContextHolder.getLocale()));
            }
            return "redirect:/edit-profile";
        } catch (ValidationException ex) {
            logger.warn("Profile update failed for user {}: {}", form.getUsername(), ex.getMessage());
            model.addAttribute("editProfileForm", form);
            addSecretQuestions(model);
            model.addAttribute("error", ex.getMessage());
            return "edit-profile";
        }
    }

    /**
     * Displays the admin panel page for admin users.
     *
     * @return View name for admin panel page
     */
    @GetMapping("/admin/panel")
    public String adminPanel() {
        return "admin-panel";
    }

    /**
     * Displays the admin users page for admin users.
     *
     * @return View name for admin users page
     */
    @GetMapping("/admin/users")
    public String adminUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "firstName") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {
        String safeSort = sanitizeAdminUserSort(sort);
        String safeDirection = sanitizeDirection(direction);
        Pageable pageable = pageable(page, size, safeSort, safeDirection, 5, 100, "firstName");
        UserService.AdminUsersPageData pageData = userService.buildAdminUsersPageData(pageable, authentication);

        model.addAttribute("users", pageData.usersPage().getContent());
        addPageMeta(model, pageData.usersPage(), size);
        model.addAttribute("sort", safeSort);
        model.addAttribute("direction", safeDirection);
        model.addAttribute("secretQuestionsMap", pageData.secretQuestionsMap());
        model.addAttribute("userTypesMap", pageData.userTypeNamesMap());
        if (pageData.currentUserId() != null) {
            model.addAttribute("currentUserId", pageData.currentUserId());
        }
        return "admin-users";
    }

    /**
     * Handles the submission of the add user form from admin panel.
     *
     * @param form               RegisterFormDTO containing user data
     * @param redirectAttributes Redirect attributes for feedback messages
     * @return Redirect or view name based on outcome
     */
    @PostMapping("/admin/users/add")
    public String addUserFromAdmin(@Valid @ModelAttribute("registerForm") RegisterFormDTO form,
            BindingResult bindingResult,
            @RequestParam(value = "returnTo", required = false) String returnTo,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showAdminAddUserForm(model, form, null, returnTo);
        }
        try {
            userService.registerFromAdmin(form);
            redirectAttributes.addFlashAttribute("success",
                    messageSource.getMessage("admin.action.add.success", null, LocaleContextHolder.getLocale()));
            if ("privileges".equalsIgnoreCase(returnTo)) {
                return "redirect:/admin/privileges";
            }
            return "redirect:/admin/users";
        } catch (Exception ex) {
            return showAdminAddUserForm(model, form, ex.getMessage(), returnTo);
        }
    }

    /**
     * Displays the add user form for admin users.
     *
     * @return View name for add user page
     */
    @GetMapping("/admin/users/add")
    public String showAddUserForm(Model model,
            @RequestParam(value = "returnTo", required = false) String returnTo) {
        return showAdminAddUserForm(model, new RegisterFormDTO(), null, returnTo);
    }

    /**
     * Handles the deletion of selected users from admin panel.
     *
     * @param userIds            List of user IDs to delete
     * @param redirectAttributes Redirect attributes for feedback messages
     * @return Redirect URL for admin users page
     */
    @PostMapping("/admin/users/delete")
    public String deleteUsers(@RequestParam(value = "userIds", required = false) List<Long> userIds,
            @RequestParam(defaultValue = "false") boolean confirm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "firstName") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(userIds);
        String adminUsersRedirect = buildAdminUsersRedirect(page, size, sort, direction);
        if (safeIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.action.delete.noselection", null, LocaleContextHolder.getLocale()));
            return adminUsersRedirect;
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "delete.user.confirmation",
                    "/admin/users/delete",
                    adminUsersRedirect.replace("redirect:", ""),
                    hiddenParamsForIds("userIds", safeIds, Map.of(
                            "page", String.valueOf(safePage(page)),
                            "size", String.valueOf(safePageSize(size)),
                            "sort", sort,
                            "direction", direction)));
        }
        try {
            userService.deleteUsersByIds(safeIds, userService.resolveCurrentUserId(authentication));
            redirectAttributes.addFlashAttribute("success",
                    messageSource.getMessage("admin.action.delete.success", null, LocaleContextHolder.getLocale()));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return adminUsersRedirect;
    }

    /**
     * Helper method to render the admin edit user form.
     *
     * @param model        model to populate view attributes
     * @param form         form data
     * @param errorMessage error message (optional)
     * @param returnTo     return path (optional)
     * @return view name for admin edit user
     */
    private String showAdminEditUserForm(Model model, AdminEditUserFormDTO form, String errorMessage, String returnTo) {
        model.addAttribute("user", form);
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
        }
        model.addAttribute("returnTo", returnTo);
        addAdminUserFormOptions(model);
        return "admin-edit-user";
    }

    /**
     * Helper method to render the admin add user form.
     *
     * @param model        model to populate view attributes
     * @param form         form data
     * @param errorMessage error message (optional)
     * @param returnTo     return path (optional)
     * @return view name for admin add user
     */
    private String showAdminAddUserForm(Model model, RegisterFormDTO form, String errorMessage, String returnTo) {
        model.addAttribute("registerForm", form);
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
        }
        model.addAttribute("returnTo", returnTo);
        addAdminUserFormOptions(model);
        return "admin-add-user";
    }

    /**
     * Adds options for admin user forms to the model.
     *
     * @param model model to populate view attributes
     */
    private void addAdminUserFormOptions(Model model) {
        addSecretQuestions(model);
        model.addAttribute("userTypes", userTypeService.findAll());
    }

    /**
     * Adds secret questions to the model for forms.
     *
     * @param model model to populate view attributes
     */
    private void addSecretQuestions(Model model) {
        model.addAttribute("secretQuestions", secretQuestionService.findAll());
    }

    private String buildAdminUsersRedirect(int page, int size, String sort, String direction) {
        int safePage = safePage(page);
        int safeSize = safePageSize(size);
        String safeSort = sanitizeAdminUserSort(sort);
        String safeDirection = sanitizeDirection(direction);
        return "redirect:/admin/users?page=" + safePage
                + "&size=" + safeSize
                + "&sort=" + safeSort
                + "&direction=" + safeDirection;
    }

    private String sanitizeAdminUserSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return "firstName";
        }
        String requested = sort.trim();
        return ADMIN_USER_SORT_FIELDS.contains(requested) ? requested : "firstName";
    }

    private String sanitizeDirection(String direction) {
        return "desc".equalsIgnoreCase(direction) ? "desc" : "asc";
    }

    private String buildSearchRedirect(String type, String query, String matchType) {
        String safeType = type == null ? "Title" : type.trim();
        String safeQuery = query == null ? "" : query.trim();
        String safeMatchType = (matchType == null || matchType.isBlank()) ? "contains" : matchType.trim();

        String path;
        UriComponentsBuilder builder;
        if ("Author".equalsIgnoreCase(safeType)) {
            boolean exactMatch = "exact".equalsIgnoreCase(safeMatchType);
            var authorPage = authorService.searchAuthors(safeQuery, safeMatchType, PageRequest.of(0, 100));
            if (exactMatch && authorPage.getTotalElements() == 1 && !authorPage.getContent().isEmpty()) {
                path = "/authors/" + authorPage.getContent().get(0).getId();
                builder = UriComponentsBuilder.fromPath(path);
            } else {
                path = "/authors";
                builder = UriComponentsBuilder.fromPath(path)
                        .queryParam("q", safeQuery)
                        .queryParam("matchType", safeMatchType);
            }
        } else if ("Publisher".equalsIgnoreCase(safeType)) {
            boolean exactMatch = "exact".equalsIgnoreCase(safeMatchType);
            var publisherPage = publisherService.searchPublishers(safeQuery, safeMatchType, PageRequest.of(0, 100));
            if (exactMatch && publisherPage.getTotalElements() == 1 && !publisherPage.getContent().isEmpty()) {
                path = "/publishers/" + publisherPage.getContent().get(0).getId();
                builder = UriComponentsBuilder.fromPath(path);
            } else {
                path = "/publishers";
                builder = UriComponentsBuilder.fromPath(path)
                        .queryParam("q", safeQuery)
                        .queryParam("matchType", safeMatchType);
            }
        } else {
            path = "/books";
            builder = UriComponentsBuilder.fromPath(path)
                    .queryParam("q", safeQuery)
                    .queryParam("matchType", safeMatchType)
                    .queryParam("returnTo", "/dashboard");
        }
        return "redirect:" + builder.build().encode().toUriString();
    }

}
