package com.example.bookportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResultDTO {
    private List<BookSummaryDTO> items;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    // Optional: lists of matching authors/publishers for UI
    private List<String> matchingAuthors;
    private List<String> matchingPublishers;
    public SearchResultDTO(List<BookSummaryDTO> items, long totalElements, int totalPages, int page, int size) {
        this.items = items;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
        this.matchingAuthors = null;
        this.matchingPublishers = null;
    }
}
