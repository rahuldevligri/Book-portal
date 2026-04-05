package com.example.bookportal.dto;

import com.example.bookportal.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * DTO for user registration form data.
 * Contains validation annotations for all fields.
 */
@Data
@PasswordMatches
public class RegisterFormDTO {

    /**
     * UserEntity's first name.
     */
    @NotBlank(message = "{NotBlank.firstName}")
    @Size(min = 2, max = 30, message = "{Size.firstName}")
    private String firstName;


    /**
     * UserEntity's last name.
     */
    @NotBlank(message = "{NotBlank.lastName}")
    @Size(min = 2, max = 30, message = "{Size.lastName}")
    private String lastName;


    /**
     * UserEntity's email address.
     */
    @NotBlank(message = "{NotBlank.email}")
    @Email(message = "{Email.email}")
    @Pattern(regexp = "^(?![ab]@[ab]$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{Pattern.email}")
    private String email;


    /**
     * UserEntity's username.
     */
    @NotBlank(message = "{NotBlank.username}")
    @Size(min = 3, max = 30, message = "{Size.username}")
    private String username;


    /**
     * UserEntity's password.
     */
    @NotBlank(message = "{NotBlank.password}")
    @Size(min = 8, max = 100, message = "{Size.password}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "{Pattern.password}")
    private String password;
    /**
     * Confirmation of the user's password.
     */
    @NotBlank(message = "{NotBlank.confirmPassword}")
    private String confirmPassword;
    /**
     * ID of the selected secret question.
     */
    @NotNull(message = "{NotNull.secretQuestionId}")
    private Long secretQuestionId;
    /**
     * Answer to the secret question.
     */
    @NotBlank(message = "{NotBlank.secretAnswer}")
    private String secretAnswer;
    /**
     * UserEntity type ID (optional, for admin registration).
     */
    private Long userTypeId;

}
