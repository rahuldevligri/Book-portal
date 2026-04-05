package com.example.bookportal.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data

public class EditProfileFormDTO {

    @NotBlank(message = "{NotBlank.username}")
    @Size(min = 3, max = 30, message = "{Size.username}")
    private String username;

    @NotBlank(message = "{NotBlank.firstName}")
    @Size(min = 2, max = 30, message = "{Size.firstName}")
    private String firstName;

    @NotBlank(message = "{NotBlank.lastName}")
    @Size(min = 2, max = 30, message = "{Size.lastName}")
    private String lastName;

    @NotBlank(message = "{NotBlank.email}")
    @Email(message = "{Email.email}")
    private String email;

    @NotNull(message = "{NotNull.secretQuestionId}")
    private Long secretQuestionId;

    @Size(max = 255, message = "{Size.secretAnswer}")
    private String secretAnswer;
}
