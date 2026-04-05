package com.example.bookportal.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupApisResponse {
    private Long groupId;
    private List<Long> apiIds;
    private List<ApiInfo> apis;

    @Data
    public static class ApiInfo {
        private Long id;
        private String apiUrl;
    }
}
