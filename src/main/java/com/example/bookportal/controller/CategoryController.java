package com.example.bookportal.controller;

import com.example.bookportal.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for category-related operations.
 * <p>
 * Handles listing and displaying categories for users.
 */
@Controller
@RequestMapping("/categories")
@Validated
public class CategoryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    /**
     * Displays the category listing page.
     * 
     * @param model     model to populate view attributes
     * @param page      current page number
     * @param size      page size
     * @param sort      sort field
     * @param direction sort direction
     * @return view name for category listing
     */
    @GetMapping
    public String categoryPage(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = pageable(page, size, sort, direction, 1, 100, "id");
        Page<com.example.bookportal.entity.CategoryEntity> categoriesPage = categoryService.getCategoriesPage(pageable);
        model.addAttribute("categories", categoriesPage.getContent());
        addPageMeta(model, categoriesPage, size);
        logger.info("Fetched all categories with pagination");
        return "category";
    }

    /**
     * Displays the summary page for a specific category.
     * 
     * @param id        category ID
     * @param model     model to populate view attributes
     * @param page      current page number
     * @param size      page size
     * @param sort      sort field
     * @param direction sort direction
     * @return view name for category summary
     */
    @GetMapping("/{id}")
    public String categorySummary(@PathVariable Long id, Model model,
            @RequestParam(required = false, name = "returnTo") String returnTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        model.addAttribute("backUrl", categoryService.resolveReturnTo(returnTo, "/categories"));
        model.addAttribute("bookCount", categoryService.getCategoryBookCount(id));
        Pageable pageable = pageable(page, size, sort, direction, 1, 100, "id");
        Page<com.example.bookportal.entity.BookEntity> booksPage = categoryService.getBooksByCategory(id, pageable);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("page", booksPage);
        logger.info("Fetched details for category ID: {}", id);
        return "category-summary";
    }
}
