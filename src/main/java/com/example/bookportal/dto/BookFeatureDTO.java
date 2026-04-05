package com.example.bookportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for BookEntity Feature with its value
 */
@Data
@AllArgsConstructor
public class BookFeatureDTO {
    private Long featureId;
    private String featureName;
    private String value;
}
