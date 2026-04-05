package com.example.bookportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotFormDTO {

    @NotBlank(message = "forgot.email.invalid")
    @Email(message = "forgot.email.invalid")
    private String email;
}

