package com.example.bookportal.service.impl;

import com.example.bookportal.entity.Author;
import com.example.bookportal.repository.AuthorRepository;
import com.example.bookportal.repository.BookRepository;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;
import com.example.bookportal.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository,
                             BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public Author getAuthorById(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
    }

    @Override
    public List<CategoryBookCountProjection> getCategoryWiseBooks(Long authorId) {
        return bookRepository.findCategoryWiseBookCountByAuthor(authorId);
    }
}
