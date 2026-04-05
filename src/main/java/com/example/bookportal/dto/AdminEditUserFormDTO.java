package com.example.bookportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminEditUserFormDTO {
    private Long id;

    @NotBlank(message = "{NotBlank.firstName}")
    @Size(min = 2, max = 30, message = "{Size.firstName}")
    @Pattern(regexp = "^[A-Za-z]+$", message = "{Pattern.firstName}")
    private String firstName;

    @NotBlank(message = "{NotBlank.lastName}")
    @Size(min = 2, max = 30, message = "{Size.lastName}")
    @Pattern(regexp = "^[A-Za-z]+$", message = "{Pattern.lastName}")
    private String lastName;

    @NotBlank(message = "{NotBlank.email}")
    @Email(message = "{Email.email}")
    @Pattern(regexp = "^(?![ab]@[ab]$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{Pattern.email}")
    private String email;

    @NotBlank(message = "{NotBlank.username}")
    @Size(min = 3, max = 30, message = "{Size.username}")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "{Pattern.username}")
    private String userName;

    @Pattern(regexp = "^$|(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "{Pattern.password.optional}")
    private String password;

    @NotNull(message = "{NotNull.secretQuestionId}")
    private Long secretQuestionId;

    @Size(max = 255, message = "{Size.secretAnswer}")
    private String secretAnswer;

    @NotNull(message = "{NotNull.userTypeId}")
    private Long userTypeId;
    
    private boolean active;
}
