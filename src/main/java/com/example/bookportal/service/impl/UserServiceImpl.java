package com.example.bookportal.service.impl;

import com.example.bookportal.dto.AdminEditUserFormDTO;
import com.example.bookportal.dto.ChangePasswordFormDTO;
import com.example.bookportal.dto.EditProfileFormDTO;
import com.example.bookportal.dto.RegisterFormDTO;
import com.example.bookportal.dto.UserInfoDTO;
import com.example.bookportal.entity.UserEntity;
import com.example.bookportal.exception.ResourceNotFoundException;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.repository.UserRepository;
import com.example.bookportal.repository.UserTypeRepository;
import com.example.bookportal.service.SecretQuestionService;
import com.example.bookportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Service class for managing user-related operations such as registration, password change,
 * profile update, user retrieval, and admin user management.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecretQuestionService secretQuestionService;
    @Autowired
    private UserTypeRepository userTypeRepository;
    /**
     * Registers a new user with the provided form data.
     *
     * @param form registration form DTO
     * @throws ValidationException if validation fails
     */
    @Override
    public void register(RegisterFormDTO form) {
        logger.info("Registering user: {}", form.getUsername());

        String normalizedUsername = normalizeIdentityValue(form.getUsername());
        String normalizedEmail = normalizeIdentityValue(form.getEmail());

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            logger.warn("Password mismatch for user: {}", form.getUsername());
            throw new ValidationException(messageSource.getMessage("password.mismatch", null, LocaleContextHolder.getLocale()));
        }
        ensureEmailAvailable(normalizedEmail, null);
        ensureUsernameAvailable(normalizedUsername);
        validateEmailFormat(normalizedEmail);
        validatePasswordComplexity(form.getPassword(), messageSource.getMessage("Pattern.newPassword", null, LocaleContextHolder.getLocale()));

        UserEntity user = new UserEntity();
        setIdentityFields(user, normalizedUsername, form.getFirstName(), form.getLastName(), normalizedEmail);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        setSecretFields(user, form.getSecretQuestionId(), form.getSecretAnswer());
        user.setUserTypeId(null);       // default/unknown

        userRepository.save(user);
        logger.info("UserEntity registered: {}", form.getUsername());
    }

    /**
     * Changes the password for the specified user.
     *
     * @param username the username
     * @param form change password form DTO
     * @throws ValidationException if validation fails
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public void changePassword(String username, ChangePasswordFormDTO form) {
        logger.info("Password change attempt for user: {}", username);

        UserEntity user = userRepository.findByUserNameIgnoreCase(username).orElseThrow(() -> {
            logger.warn("UserEntity not found for password change: {}", username);
            return new ResourceNotFoundException("UserEntity not found: " + username);
        });

        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            logger.warn("Invalid old password for user: {}", username);
            throw new ValidationException(messageSource.getMessage("old.password.invalid", null, LocaleContextHolder.getLocale()));
        }
        validatePasswordComplexity(form.getNewPassword(), messageSource.getMessage("Pattern.newPassword", null, LocaleContextHolder.getLocale()));
        // Prevent old password and new password from being the same
        if (form.getOldPassword().equals(form.getNewPassword())) {
            throw new ValidationException(messageSource.getMessage("password.sameasold", null, LocaleContextHolder.getLocale()));
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new ValidationException(messageSource.getMessage("password.mismatch", null, LocaleContextHolder.getLocale()));
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
        logger.info("Password changed for user: {}", username);
    }

    /**
     * Finds a user by username.
     *
     * @param username username to search
     * @return user entity
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public UserEntity findByUsername(String username) {
        logger.info("Finding user by username: {}", username);

        return userRepository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> {
                    logger.warn("UserEntity not found: {}", username);
                    return new ResourceNotFoundException("UserEntity not found: " + username);
                });
    }

    /**
     * Updates the profile of a user from the provided form.
     *
     * @param form profile edit form
     * @return
     * @throws ValidationException       if validation fails
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public boolean updateProfileFromForm(EditProfileFormDTO form) {
        logger.info("Updating profile for user: {}", form.getUsername());

        UserEntity user = userRepository.findByUserNameIgnoreCase(form.getUsername())
                .orElseThrow(() -> {
                    logger.warn("UserEntity not found for profile update: {}", form.getUsername());
                    return new ResourceNotFoundException("UserEntity not found");
                });

        boolean changed = false;

        if (!user.getFirstName().equals(form.getFirstName())) changed = true;
        if (!user.getLastName().equals(form.getLastName())) changed = true;
        if (!user.getEmail().equals(form.getEmail())) changed = true;
        if (user.getSecretQuestionId() == null ? form.getSecretQuestionId() != null : !user.getSecretQuestionId().equals(form.getSecretQuestionId())) changed = true;
        if (user.getSecretAnswer() == null ? form.getSecretAnswer() != null : !user.getSecretAnswer().equals(form.getSecretAnswer())) changed = true;

        if (!changed) {
            logger.info("No changes detected for user: {}. Skipping save.", form.getUsername());
            return false;
        }

        String normalizedEmail = normalizeIdentityValue(form.getEmail());
        ensureEmailAvailable(normalizedEmail, user.getId());
        validateEmailFormat(normalizedEmail);

        setIdentityFields(user, user.getUserName(), form.getFirstName(), form.getLastName(), normalizedEmail);
        setSecretFields(user, form.getSecretQuestionId(), form.getSecretAnswer());

        userRepository.save(user);
        logger.info("Profile updated for user: {}", form.getUsername());
        return true;
    }

    /**
     * Retrieves paginated users.
     *
     * @param pageable pagination information
     * @return page of users
     */
    @Override
    public Page<UserEntity> findUsersPage(Pageable pageable) {
        logger.info("Fetching users page: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        if (isUserTypeSort(pageable)) {
            return findUsersPageSortedByUserType(pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Override
    public AdminUsersPageData buildAdminUsersPageData(Pageable pageable, Authentication authentication) {
        Page<UserEntity> usersPage = findUsersPage(pageable);
        Map<Long, String> questionMap = secretQuestionService.findAllLocalizedQuestionMap();
        Map<Long, String> userTypeNamesMap = new HashMap<>();
        userTypeRepository.findAll().forEach(type -> userTypeNamesMap.put(type.getId(), type.getType()));
        Long currentUserId = resolveCurrentUserId(authentication);
        return new AdminUsersPageData(usersPage, questionMap, userTypeNamesMap, currentUserId);
    }

    /**
     * Finds a user by ID.
     *
     * @param id user ID
     * @return user entity
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public UserEntity findById(Long id) {
        logger.info("Finding user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserEntity not found: " + id));
    }

    /**
     * Updates a user from the admin edit user form.
     *
     * @param form admin edit form
     * @return
     * @throws ValidationException       if validation fails
     * @throws ResourceNotFoundException if user is not found
     */
    @Override
    public boolean updateUserFromAdminForm(AdminEditUserFormDTO form) {
        logger.info("Admin updating user with id: {}", form.getId());
        UserEntity user = userRepository.findById(form.getId())
            .orElseThrow(() -> new ResourceNotFoundException("UserEntity not found: " + form.getId()));

        boolean changed = false;
        String oldFirstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String newFirstName = form.getFirstName() == null ? "" : form.getFirstName().trim();
        if (!oldFirstName.equalsIgnoreCase(newFirstName)) changed = true;

        String oldLastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String newLastName = form.getLastName() == null ? "" : form.getLastName().trim();
        if (!oldLastName.equalsIgnoreCase(newLastName)) changed = true;

        String oldEmail = user.getEmail() == null ? "" : user.getEmail().trim();
        String newEmail = form.getEmail() == null ? "" : form.getEmail().trim();
        if (!oldEmail.equalsIgnoreCase(newEmail)) changed = true;

        String oldUserName = user.getUserName() == null ? "" : user.getUserName().trim();
        String newUserName = form.getUserName() == null ? "" : form.getUserName().trim();
        if (!oldUserName.equalsIgnoreCase(newUserName)) changed = true;

        if (user.getSecretQuestionId() == null ? form.getSecretQuestionId() != null : !user.getSecretQuestionId().equals(form.getSecretQuestionId())) changed = true;

        if (user.getUserTypeId() == null ? form.getUserTypeId() != null : !user.getUserTypeId().equals(form.getUserTypeId())) changed = true;

        // Only mark as changed if password is provided and differs from the current one
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            // Passwords are hashed, so we can't compare directly. Always treat as changed if provided.
            changed = true;
        }

        // Only mark as changed if secret answer is provided and differs from the current one
        String oldSecretAnswer = user.getSecretAnswer() == null ? "" : user.getSecretAnswer().trim();
        String newSecretAnswer = form.getSecretAnswer() == null ? "" : form.getSecretAnswer().trim();
        if (!newSecretAnswer.isEmpty() && !oldSecretAnswer.equalsIgnoreCase(newSecretAnswer)) changed = true;

        if (!changed) {
            logger.info("No changes detected for admin user update: {}. Skipping save.", form.getId());
            return false;
        }

        String normalizedUserName = normalizeIdentityValue(form.getUserName());
        String normalizedEmail = normalizeIdentityValue(form.getEmail());
        ensureUsernameAvailable(normalizedUserName, user.getId());
        ensureEmailAvailable(normalizedEmail, user.getId());
        validateEmailFormat(normalizedEmail);

        setIdentityFields(user, normalizedUserName, form.getFirstName(), form.getLastName(), normalizedEmail);
        setSecretFields(user, form.getSecretQuestionId(), form.getSecretAnswer());
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        user.setUserTypeId(form.getUserTypeId());

        userRepository.save(user);

        logger.info("UserEntity updated by admin: {}", form.getId());
        return true;
    }

    /**
     * Registers a new user from the admin panel.
     *
     * @param form registration form
     * @throws ValidationException if validation fails
     */
    @Override
    public void registerFromAdmin(RegisterFormDTO form) {
        logger.info("Admin registering user: {}", form.getUsername());

        String normalizedUsername = normalizeIdentityValue(form.getUsername());
        String normalizedEmail = normalizeIdentityValue(form.getEmail());

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new ValidationException(messageSource.getMessage("password.mismatch", null, LocaleContextHolder.getLocale()));
        }
        ensureEmailAvailable(normalizedEmail, null);
        ensureUsernameAvailable(normalizedUsername);
        validateEmailFormat(normalizedEmail);
        validatePasswordComplexity(form.getPassword(), messageSource.getMessage("password.rule", null, LocaleContextHolder.getLocale()));

        UserEntity user = new UserEntity();
        setIdentityFields(user, normalizedUsername, form.getFirstName(), form.getLastName(), normalizedEmail);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        setSecretFields(user, form.getSecretQuestionId(), form.getSecretAnswer());
        user.setUserTypeId(form.getUserTypeId() != null ? form.getUserTypeId() : 2L);

        userRepository.save(user);
        logger.info("UserEntity registered by admin: {}", form.getUsername());
    }

    /**
     * Deletes users by their IDs.
     *
     * @param userIds list of user IDs to delete
     * @throws ResourceNotFoundException if any user is not found
     */
    @Override
    public void deleteUsersByIds(List<Long> userIds) {
        deleteUsersByIds(userIds, null);
    }

    @Override
    public void deleteUsersByIds(List<Long> userIds, Long currentUserId) {
        logger.info("Deleting users with IDs: {}", userIds);
        for (Long id : userIds) {
            if (currentUserId != null && currentUserId.equals(id)) {
                throw new ValidationException(messageSource.getMessage("admin.user.delete.self", null, LocaleContextHolder.getLocale()));
            }
            if (!userRepository.existsById(id)) {
                throw new ResourceNotFoundException("UserEntity not found: " + id);
            }
        }
        userRepository.deleteAllById(userIds);
        logger.info("Users deleted: {}", userIds);
    }

    @Override
    public void resetPasswordByEmail(String email, String newPassword) {
        String normalizedEmail = normalizeIdentityValue(email);
        String normalizedNewPassword = newPassword == null ? null : newPassword.trim();

        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        validatePasswordComplexity(normalizedNewPassword,
                messageSource.getMessage("password.rule", null, LocaleContextHolder.getLocale()));
        user.setPassword(passwordEncoder.encode(normalizedNewPassword));
        userRepository.save(user);
    }

    @Override
    public String resolveCurrentUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserInfoDTO userInfoDTO && userInfoDTO.getUserName() != null
                && !userInfoDTO.getUserName().isBlank()) {
            return userInfoDTO.getUserName();
        }
        String name = authentication.getName();
        if (name != null && name.startsWith("UserInfoDTO(") && name.contains("userName=")) {
            int start = name.indexOf("userName=") + "userName=".length();
            int end = name.indexOf(',', start);
            if (end > start) {
                return name.substring(start, end).trim();
            }
        }
        return name;
    }

    @Override
    public Long resolveCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserInfoDTO userInfoDTO && userInfoDTO.getId() != null) {
            return userInfoDTO.getId();
        }
        String currentUsername = resolveCurrentUsername(authentication);
        if (currentUsername == null || currentUsername.isBlank()) {
            return null;
        }
        return findByUsername(currentUsername).getId();
    }

    @Override
    public AdminEditUserFormDTO buildAdminEditForm(Long id) {
        UserEntity user = findById(id);
        AdminEditUserFormDTO form = new AdminEditUserFormDTO();
        form.setId(user.getId());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setEmail(user.getEmail());
        form.setUserName(user.getUserName());
        form.setSecretQuestionId(user.getSecretQuestionId());
        form.setUserTypeId(user.getUserTypeId());
        // Password and secretAnswer stay blank by design so existing values are not exposed.
        return form;
    }

    @Override
    public EditProfileFormDTO buildEditProfileForm(String username) {
        UserEntity user = findByUsername(username);
        EditProfileFormDTO form = new EditProfileFormDTO();
        form.setUsername(user.getUserName());
        form.setEmail(user.getEmail());
        form.setSecretQuestionId(user.getSecretQuestionId());
        form.setSecretAnswer(user.getSecretAnswer());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        return form;
    }

    @Override
    public boolean isSecretAnswerRequiredForAdminEdit(Long userId, Long newSecretQuestionId, String newSecretAnswer) {
        UserEntity existingUser = findById(userId);
        boolean changedQuestion = existingUser.getSecretQuestionId() == null
                ? newSecretQuestionId != null
                : !existingUser.getSecretQuestionId().equals(newSecretQuestionId);
        return changedQuestion && (newSecretAnswer == null || newSecretAnswer.isBlank());
    }

    @Override
    public boolean isSecretAnswerRequiredForProfileEdit(String username, Long newSecretQuestionId, String newSecretAnswer) {
        UserEntity existingUser = findByUsername(username);
        boolean changedQuestion = existingUser.getSecretQuestionId() == null
                ? newSecretQuestionId != null
                : !existingUser.getSecretQuestionId().equals(newSecretQuestionId);
        return changedQuestion && (newSecretAnswer == null || newSecretAnswer.isBlank());
    }

    private void ensureUsernameAvailable(String username) {
        ensureUsernameAvailable(username, null);
    }

    private void ensureUsernameAvailable(String username, Long excludedUserId) {
        userRepository.findByUserNameIgnoreCase(username)
                .filter(existing -> excludedUserId == null || !excludedUserId.equals(existing.getId()))
                .ifPresent(existing -> {
                    throw new ValidationException(messageSource.getMessage("register.username.exists", null, LocaleContextHolder.getLocale()));
                });
    }

    private void ensureEmailAvailable(String email, Long excludedUserId) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> excludedUserId == null || !excludedUserId.equals(existing.getId()))
                .ifPresent(existing -> {
                    throw new ValidationException(messageSource.getMessage("register.email.exists", null, LocaleContextHolder.getLocale()));
                });
    }

    private void validateEmailFormat(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new ValidationException(messageSource.getMessage("Pattern.email", null, LocaleContextHolder.getLocale()));
        }
    }

    private void validatePasswordComplexity(String password, String errorMessage) {
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new ValidationException(errorMessage);
        }
    }

    private void setIdentityFields(UserEntity user, String userName, String firstName, String lastName, String email) {
        user.setUserName(normalizeIdentityValue(userName));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(normalizeIdentityValue(email));
    }

    private String normalizeIdentityValue(String value) {
        return value == null ? null : value.trim();
    }

    private void setSecretFields(UserEntity user, Long secretQuestionId, String secretAnswer) {
        user.setSecretQuestionId(secretQuestionId);
        if (secretAnswer != null && !secretAnswer.isEmpty()) {
            user.setSecretAnswer(secretAnswer);
        }
    }

    private boolean isUserTypeSort(Pageable pageable) {
        if (pageable == null) {
            return false;
        }
        Sort sort = pageable.getSort();
        if (sort == null || !sort.isSorted()) {
            return false;
        }
        return sort.stream().anyMatch(order -> "userTypeId".equals(order.getProperty()));
    }

    private Page<UserEntity> findUsersPageSortedByUserType(Pageable pageable) {
        List<UserEntity> allUsers = userRepository.findAll();
        Long adminTypeId = resolveAdminTypeId();
        Sort.Order order = pageable.getSort().getOrderFor("userTypeId");
        boolean ascending = order == null || order.getDirection().isAscending();

        Comparator<UserEntity> comparator = Comparator
                .comparingInt((UserEntity user) -> userTypeRank(user == null ? null : user.getUserTypeId(), adminTypeId, ascending))
                .thenComparing(user -> safeString(user == null ? null : user.getFirstName()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(user -> safeString(user == null ? null : user.getLastName()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(user -> safeString(user == null ? null : user.getUserName()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(user -> user == null || user.getId() == null ? Long.MAX_VALUE : user.getId());

        allUsers.sort(comparator);
        int fromIndex = Math.min((int) pageable.getOffset(), allUsers.size());
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), allUsers.size());
        return new PageImpl<>(allUsers.subList(fromIndex, toIndex), pageable, allUsers.size());
    }

    private int userTypeRank(Long userTypeId, Long adminTypeId, boolean ascending) {
        boolean isAdmin = adminTypeId != null && adminTypeId.equals(userTypeId);
        if (ascending) {
            return isAdmin ? 0 : 1;
        }
        return isAdmin ? 1 : 0;
    }

    private Long resolveAdminTypeId() {
        return userTypeRepository.findAll().stream()
                .filter(type -> type.getType() != null && "admin".equalsIgnoreCase(type.getType().trim()))
                .map(type -> type.getId())
                .findFirst()
                .orElse(1L);
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }
}
