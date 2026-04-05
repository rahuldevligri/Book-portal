package com.example.bookportal.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupPrivilegesDTO {
    private Long id;
    private String groupName; // raw stored name or message key
    private String displayName; // localized name resolved from messages when possible
    private List<String> apis;
}
