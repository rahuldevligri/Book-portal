package com.example.bookportal.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserInfoDTO {
    private String token;
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private Long userTypeId;
    private Set<String> authorizedApiUrls;
}
