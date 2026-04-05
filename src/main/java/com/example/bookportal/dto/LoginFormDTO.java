package com.example.bookportal.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginFormDTO {
    @NotEmpty(message = "{NotEmpty.username}")
    private String username;

    @NotEmpty(message = "{NotEmpty.password}")
    private String password;
}
