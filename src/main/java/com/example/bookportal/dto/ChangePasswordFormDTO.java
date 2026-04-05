package com.example.bookportal.dto;

import com.example.bookportal.validation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class ChangePasswordFormDTO {

    @NotBlank(message = "{NotBlank.oldPassword}")
    private String oldPassword;

    @NotBlank(message = "{NotBlank.newPassword}")
    @Size(min = 8, max = 100, message = "{Size.newPassword}")
    @jakarta.validation.constraints.Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "{Pattern.newPassword}")
    private String newPassword;

    @NotBlank(message = "{NotBlank.confirmPassword}")
    private String confirmPassword;
}
