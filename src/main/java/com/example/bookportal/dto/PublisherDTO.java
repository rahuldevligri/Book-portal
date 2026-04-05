package com.example.bookportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublisherDTO {
    private Long id;

    @NotBlank(message = "{NotBlank.name}")
    private String name;

    @NotBlank(message = "{NotBlank.address}")
    private String address;

    @NotBlank(message = "{NotBlank.telephone}")
    private String telephone;

    @NotBlank(message = "{NotBlank.fax}")
    private String fax;

    @NotBlank(message = "{NotBlank.email}")
    @Email(message = "{Email.email}")
    private String email;

    @NotBlank(message = "{NotBlank.webSite}")
    private String webSite;
}
