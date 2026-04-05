package com.example.bookportal.service;

import com.example.bookportal.dto.ChangePasswordFormDTO;
import com.example.bookportal.dto.EditProfileFormDTO;
import com.example.bookportal.dto.RegisterFormDTO;
import com.example.bookportal.dto.AdminEditUserFormDTO;
import com.example.bookportal.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface UserService {
    record AdminUsersPageData(Page<UserEntity> usersPage,
                              Map<Long, String> secretQuestionsMap,
                              Map<Long, String> userTypeNamesMap,
                              Long currentUserId) {
    }

    /**
     * Registers a new user with the provided form data.
     *
     * @param form registration form DTO
     */
    void register(RegisterFormDTO form);

    /**
     * Changes the password for the specified user.
     *
     * @param username the username
     * @param form     change password form DTO
     */
    void changePassword(String username, ChangePasswordFormDTO form);

    /**
     * Finds a user by their username.
     *
     * @param username the username
     * @return user entity
     */
    UserEntity findByUsername(String username);

    /**
     * Updates the user profile from the provided form.
     *
     * @param form edit profile form DTO
     * @return true if update was successful
     */
    boolean updateProfileFromForm(EditProfileFormDTO form);

    /**
     * Retrieves paginated users.
     *
     * @param pageable pagination information
     * @return page of users
     */
    Page<UserEntity> findUsersPage(Pageable pageable);

    /**
     * Builds the admin users list view data.
     *
     * @param pageable       pagination information
     * @param authentication current authentication
     * @return admin users page data
     */
    AdminUsersPageData buildAdminUsersPageData(Pageable pageable, Authentication authentication);

    /**
     * Finds a user by their ID.
     *
     * @param id user ID
     * @return user entity
     */
    UserEntity findById(Long id);

    /**
     * Updates a user from admin form data.
     *
     * @param form admin edit user form DTO
     * @return true if update was successful
     */
    boolean updateUserFromAdminForm(AdminEditUserFormDTO form);

    /**
     * Registers a user from admin with the provided form data.
     *
     * @param form registration form DTO
     */
    void registerFromAdmin(RegisterFormDTO form);

    /**
     * Deletes users by their IDs.
     *
     * @param userIds list of user IDs
     */
    void deleteUsersByIds(List<Long> userIds);

    /**
     * Deletes users by their IDs with optional protection for the current user.
     *
     * @param userIds        list of user IDs
     * @param currentUserId  authenticated user id to protect from self-delete
     */
    void deleteUsersByIds(List<Long> userIds, Long currentUserId);

    /**
     * Resolves the current username from spring security authentication context.
     *
     * @param authentication spring security authentication
     * @return resolved username, or null when unavailable
     */
    String resolveCurrentUsername(Authentication authentication);

    /**
     * Resolves the current authenticated user id.
     *
     * @param authentication spring security authentication
     * @return user id, or null when unavailable
     */
    Long resolveCurrentUserId(Authentication authentication);

    /**
     * Builds admin edit form from persisted user data.
     *
     * @param id user id
     * @return populated admin edit form
     */
    AdminEditUserFormDTO buildAdminEditForm(Long id);

    /**
     * Builds profile edit form from persisted user data.
     *
     * @param username user name
     * @return populated edit profile form
     */
    EditProfileFormDTO buildEditProfileForm(String username);

    /**
     * Checks if a secret answer is required for admin user edit request.
     *
     * @param userId               target user id
     * @param newSecretQuestionId  new secret question id from form
     * @param newSecretAnswer      new secret answer from form
     * @return true when secret answer is required and missing
     */
    boolean isSecretAnswerRequiredForAdminEdit(Long userId, Long newSecretQuestionId, String newSecretAnswer);

    /**
     * Checks if a secret answer is required for profile edit request.
     *
     * @param username            current username
     * @param newSecretQuestionId new secret question id from form
     * @param newSecretAnswer     new secret answer from form
     * @return true when secret answer is required and missing
     */
    boolean isSecretAnswerRequiredForProfileEdit(String username, Long newSecretQuestionId, String newSecretAnswer);

    /**
     * Resets the password for a user by email.
     *
     * @param email       the user's email
     * @param newPassword the new password
     */
    void resetPasswordByEmail(String email, String newPassword);
}
