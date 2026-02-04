package com.example.bookportal.dto;

public class CategoryBookCountDTO {

    private Long categoryId;
    private String categoryName;
    private Long bookCount;

    public CategoryBookCountDTO(Long categoryId, String categoryName, Long bookCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.bookCount = bookCount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getBookCount() {
        return bookCount;
    }
}
