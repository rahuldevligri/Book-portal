package com.example.bookportal.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPrivilegesDTO {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private List<GroupInfo> groups;

    @Data
    public static class GroupInfo {
        private Long id;
        private String groupName;
    }
}
