package com.example.bookportal.service;

import com.example.bookportal.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> getBooksByAuthorAndCategory(Long authorId, Long categoryId);
}
