package com.example.bookportal.service.impl;

import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.entity.CategoryEntity;
import com.example.bookportal.exception.ResourceNotFoundException;
import com.example.bookportal.repository.BookRepository;
import com.example.bookportal.repository.CategoryRepository;
import com.example.bookportal.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    /**
     * Retrieves paginated categories.
     *
     * @param pageable pagination information
     * @return page of categories
     */
    @Override
    public Page<CategoryEntity> getCategoriesPage(Pageable pageable) {
        logger.info("Fetching categories page: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return categoryRepository.findAll(pageable);
    }

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BookRepository bookRepository;

    /**
     * Retrieves a category by ID.
     *
     * @param id category ID
     * @return category entity
     */
    @Override
    public CategoryEntity getCategoryById(Long id) {
        logger.info("Fetching category by id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("CategoryEntity not found with id: {}", id);
                    return new ResourceNotFoundException("CategoryEntity not found with id: " + id);
                });
    }

    /**
     * Retrieves the total book count for a category.
     *
     * @param id category ID
     * @return number of books
     */
    @Override
    public Long getCategoryBookCount(Long id) {
        logger.info("Fetching book count for category id: {}", id);
        return bookRepository.countByBookCategoryId(id);
    }

    /**
     * Retrieves paginated books for a category.
     *
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByCategory(Long categoryId, Pageable pageable) {
        logger.info("Fetching books by category id: {} with pagination", categoryId);
        return bookRepository.findByBookCategoryId(categoryId, pageable);
    }

    @Override
    public String resolveReturnTo(String returnTo, String fallback) {
        if (returnTo == null || returnTo.isBlank()) {
            return fallback;
        }
        String sanitized = returnTo.trim();
        if (!sanitized.startsWith("/") || sanitized.startsWith("//") || sanitized.contains("\n") || sanitized.contains("\r")) {
            return fallback;
        }
        String lower = sanitized.toLowerCase(Locale.ROOT);
        if (lower.startsWith("/http:") || lower.startsWith("/https:")) {
            return fallback;
        }
        return sanitized;
    }
}
