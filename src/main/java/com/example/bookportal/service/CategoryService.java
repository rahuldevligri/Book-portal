package com.example.bookportal.service;

import com.example.bookportal.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing categories and related operations.
 */
public interface CategoryService {
    /**
     * Retrieves paginated categories.
     *
     * @param pageable pagination information
     * @return page of categories
     */
    Page<CategoryEntity> getCategoriesPage(Pageable pageable);

    /**
     * Retrieves a category by ID.
     *
     * @param id category ID
     * @return category entity
     */
    CategoryEntity getCategoryById(Long id);

    /**
     * Retrieves the total book count for a category.
     *
     * @param id category ID
     * @return number of books
     */
    Long getCategoryBookCount(Long id);

    /**
     * Retrieves paginated books for a category.
     *
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of books
     */
    Page<com.example.bookportal.entity.BookEntity> getBooksByCategory(Long categoryId, Pageable pageable);

    /**
     * Resolves a safe return URL with fallback for category pages.
     *
     * @param returnTo requested return URL
     * @param fallback fallback URL
     * @return safe URL
     */
    String resolveReturnTo(String returnTo, String fallback);
}
