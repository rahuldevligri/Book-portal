package com.example.bookportal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SearchRequestDTO {

    @NotBlank(message = "Search keyword is required")
    @Size(min = 2, message = "Search keyword must be at least 2 characters")
    private String query;

    @Min(value = 0, message = "Page index must be 0 or greater")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private int size = 20;

    @NotBlank(message = "Search type is required")
    @Pattern(regexp = "(?i)title|author|publisher|category|all", message = "Invalid search type")
    private String type = "all";

    private String sort;
    private String direction = "ASC";

    @Pattern(regexp = "(?i)exact|start|contains", message = "Invalid match type")
    private String matchType = "contains";
}
