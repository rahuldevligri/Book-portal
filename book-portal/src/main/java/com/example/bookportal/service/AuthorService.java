package com.example.bookportal.service;

import com.example.bookportal.entity.Author;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;

import java.util.List;

public interface AuthorService {

    List<Author> getAllAuthors();

    Author getAuthorById(Long authorId);

    List<CategoryBookCountProjection> getCategoryWiseBooks(Long authorId);
}
