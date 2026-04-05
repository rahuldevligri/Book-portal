package com.example.bookportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserTypeDTO {
    private Long id;

    @NotBlank(message = "{NotBlank.userType}")
    private String type;
}
