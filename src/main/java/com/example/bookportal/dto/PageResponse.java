package com.example.bookportal.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<T> content;
}
