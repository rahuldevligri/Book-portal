package com.example.bookportal.controller;

import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.entity.PublisherEntity;
import com.example.bookportal.service.PublisherService;
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
import java.util.List;

/**
 * Controller for publisher-related operations.
 * <p>
 * Handles listing and displaying publishers for users.
 */
@Controller
@RequestMapping("/publishers")
@Validated
public class PublisherController extends BaseController {
    private record PublisherSearchGroup(
            PublisherEntity publisher,
            List<com.example.bookportal.repository.projection.CategoryBookCountProjection> categories) {
    }

    private static final Logger logger = LoggerFactory.getLogger(PublisherController.class);

    @Autowired
    private PublisherService publisherService;

    /**
     * Displays the publisher listing page.
     * 
     * @param model     model to populate view attributes
     * @param page      current page number
     * @param size      page size
     * @param sort      sort field
     * @param direction sort direction
     * @return view name for publisher listing
     */
    @GetMapping
    public String publisherPage(
            Model model,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false, name = "matchType") String matchType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = pageable(page, size, sort, direction, 1, 100, "id");
        String normalizedQuery = query == null ? "" : query.trim();
        String normalizedMatchType = matchType == null ? "contains" : matchType.trim().toLowerCase();
        Page<PublisherEntity> publishersPage = normalizedQuery.isEmpty()
                ? publisherService.getPublishersPage(pageable)
                : publisherService.searchPublishers(normalizedQuery, matchType, pageable);
        model.addAttribute("publishers", publishersPage.getContent());
        boolean groupedSearch = !normalizedQuery.isEmpty() && !"exact".equalsIgnoreCase(normalizedMatchType);
        if (groupedSearch) {
            List<PublisherSearchGroup> groups = publishersPage.getContent().stream()
                    .map(publisher -> new PublisherSearchGroup(
                            publisher,
                            publisherService.getCategoryWiseBooks(publisher.getId())))
                    .toList();
            model.addAttribute("publisherGroups", groups);
        }
        model.addAttribute("groupedSearch", groupedSearch);
        addPageMeta(model, publishersPage, size);
        model.addAttribute("query", normalizedQuery);
        model.addAttribute("matchType", normalizedMatchType);
        model.addAttribute("type", "Publisher");
        logger.info("Fetched all publishers with pagination");
        return "publisher";
    }

    /**
     * Displays the summary page for a specific publisher.
     * 
     * @param id        publisher ID
     * @param model     model to populate view attributes
     * @param page      current page number
     * @param size      page size
     * @param sort      sort field
     * @param direction sort direction
     * @return view name for publisher summary
     */
    @GetMapping("/{id}")
    public String publisherSummary(@PathVariable Long id, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        model.addAttribute("publisher", publisherService.getPublisherById(id));
        model.addAttribute("bookCount", publisherService.getPublisherBookCount(id));
        Pageable pageable = pageable(page, size, sort, direction, 1, 100, "id");
        Page<BookEntity> booksPage = publisherService.getBooksByPublisher(id, pageable);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("categories", publisherService.getCategoryWiseBooks(id));
        model.addAttribute("page", booksPage);
        logger.info("Fetched details for publisher ID: {}", id);
        return "publisher-summary";
    }
}
