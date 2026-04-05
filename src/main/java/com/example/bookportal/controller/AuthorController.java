package com.example.bookportal.controller;

import com.example.bookportal.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * Controller for author-related operations.
 * <p>
 * Handles author listing and detail pages for users.
 */
@Controller
@RequestMapping("/authors")
@Validated
public class AuthorController extends BaseController {
    private record AuthorSearchGroup(
            com.example.bookportal.entity.AuthorEntity author,
            List<com.example.bookportal.repository.projection.CategoryBookCountProjection> categories) {
    }
    /**
     * Logger for author controller operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    /**
     * Service for author-related operations.
     */
    @Autowired
    private AuthorService authorService;

    /**
     * Displays the author listing page.
     * 
     * @param model     model to populate view attributes
     * @param page      current page number
     * @param size      page size
     * @param sort      sort field
     * @param direction sort direction
     * @return view name for author listing
     */
    @GetMapping
    public String authorPage(
            Model model,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false, name = "matchType") String matchType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        var pageable = pageable(page, size, sort, direction, 5, 100, "id");
        String normalizedQuery = query == null ? "" : query.trim();
        String normalizedMatchType = matchType == null ? "contains" : matchType.trim().toLowerCase();
        var authorsPage = normalizedQuery.isEmpty()
                ? authorService.getAuthorsPage(pageable)
                : authorService.searchAuthors(normalizedQuery, matchType, pageable);
        model.addAttribute("authors", authorsPage.getContent());
        boolean groupedSearch = !normalizedQuery.isEmpty() && !"exact".equalsIgnoreCase(normalizedMatchType);
        if (groupedSearch) {
            List<AuthorSearchGroup> groups = authorsPage.getContent().stream()
                    .map(author -> new AuthorSearchGroup(author, authorService.getCategoryWiseBooks(author.getId())))
                    .toList();
            model.addAttribute("authorGroups", groups);
        }
        model.addAttribute("groupedSearch", groupedSearch);
        addPageMeta(model, authorsPage, size);
        model.addAttribute("query", normalizedQuery);
        model.addAttribute("matchType", normalizedMatchType);
        model.addAttribute("type", "Author");
        logger.info("AuthorEntity page accessed with pagination");
        return "author";
    }

    /**
     * Displays the summary page for a specific author.
     * 
     * @param id    author ID
     * @param model model to populate view attributes
     * @return view name for author summary
     */
    @GetMapping("/{id}")
    public String authorSummary(@PathVariable Long id, Model model) {
        var author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        // Fetch category-wise books by this author
        var categories = authorService.getCategoryWiseBooks(id);
        model.addAttribute("categories", categories);
        // Optionally, fetch total book count
        var books = authorService.getBooksByAuthor(id, org.springframework.data.domain.Pageable.unpaged());
        model.addAttribute("books", books.getContent());
        model.addAttribute("bookCount", books.getTotalElements());
        logger.info("Showing summary for author id {}", id);
        return "author-summary";
    }
}
