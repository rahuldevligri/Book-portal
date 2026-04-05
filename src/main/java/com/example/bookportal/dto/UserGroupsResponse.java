package com.example.bookportal.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserGroupsResponse {
    private Long userId;
    private List<Long> groupIds;
    private List<UserPrivilegesDTO.GroupInfo> groups;
}
