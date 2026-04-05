package com.example.bookportal.repository.projection;

public interface CategoryBookCountProjection {

    Long getCategoryId();
    String getCategoryName();
    Long getBookCount();
}