package com.example.bookportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApiDTO {
    private Long id;

    @NotBlank(message = "{NotBlank.apiUrl}")
    private String apiUrl;
}
