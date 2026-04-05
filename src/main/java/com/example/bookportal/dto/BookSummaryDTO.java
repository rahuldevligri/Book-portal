package com.example.bookportal.dto;

import lombok.Data;

@Data
public class BookSummaryDTO {
    private Long id;
    private String title;
    private String authorName;
    private String categoryName;
    private String publisherName;
    private Double price;
    private String thumbnailUrl;
    private String fullImageUrl;

    public BookSummaryDTO(Long id,
                          String title,
                          String authorName,
                          String categoryName,
                          String publisherName,
                          Double price,
                          String thumbnailUrl,
                          String fullImageUrl) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.categoryName = categoryName;
        this.publisherName = publisherName;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.fullImageUrl = fullImageUrl;
    }
}
