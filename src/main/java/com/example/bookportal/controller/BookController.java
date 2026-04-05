package com.example.bookportal.controller;

import com.example.bookportal.dto.SearchRequestDTO;
import com.example.bookportal.dto.SearchResultDTO;
import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.service.SearchService;
import com.example.bookportal.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for book-related operations.
 * <p>
 * Handles book listing, searching, and details for users.
 * Provides endpoints for UI and API interactions.
 */
@Controller
@RequestMapping("/books")
public class BookController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private static final int MAX_COMPARE_BOOKS = 5;

    @Autowired
    private BookService bookService;
    @Autowired
    private SearchService searchService;

    /**
     * Displays the books page with optional filters for author, publisher, and
     * category.
     * 
     * @param authorId       author ID filter
     * @param publisherId    publisher ID filter
     * @param categoryId     category ID filter
     * @param bookIds        list of book IDs to compare
     * @param compareRequest flag for comparison
     * @param model          model to populate view attributes
     * @param page           current page number
     * @param size           page size
     * @return view name for books page
     */
    @GetMapping
    public String booksPage(
            @RequestParam(required = false, name = "authorId") Long authorId,
            @RequestParam(required = false, name = "publisherId") Long publisherId,
            @RequestParam(required = false, name = "categoryId") Long categoryId,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false, name = "matchType") String matchType,
            @RequestParam(required = false, name = "returnTo") String returnTo,
            @RequestParam(required = false, name = "featureBookId") Long featureBookId,
            @RequestParam(required = false, name = "imageBookId") Long imageBookId,
            @RequestParam(required = false, name = "bookIds") List<Long> bookIds,
            @RequestParam(required = false, name = "compareRequest", defaultValue = "false") boolean compareRequest,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        try {
            Pageable effectivePageable = pageable(page, size, sort, direction, 1, 100, "id");
            Page<BookEntity> booksPage = bookService.getBooksPage(authorId, publisherId, categoryId, effectivePageable, query, matchType);
            model.addAttribute("books", booksPage.getContent());
            model.addAttribute("bookRows", bookService.buildBookSummaries(booksPage.getContent()));
            addPageMeta(model, booksPage, size);
            model.addAttribute("authorId", authorId);
            model.addAttribute("publisherId", publisherId);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("query", query == null ? "" : query.trim());
            model.addAttribute("matchType", matchType);
            model.addAttribute("type", "Title");
            model.addAttribute("returnTo", returnTo);
            model.addAttribute("sort", sort);
            model.addAttribute("direction", direction);
            model.addAttribute("backUrl", bookService.resolveBooksBackUrl(returnTo, authorId, publisherId, categoryId));

            BookService.CompareData compareData = bookService.buildCompareData(bookIds, MAX_COMPARE_BOOKS, compareRequest);
            model.addAttribute("selectedBookIds", compareData.selectedBookIds());
            model.addAttribute("compareError", compareData.errorKey());
            model.addAttribute("showCompareModal", compareData.showModal());
            model.addAttribute("compareBooks", compareData.books());
            model.addAttribute("compareFeatureRows", compareData.featureRows());

            BookService.FeatureModalData featureModalData = bookService.buildFeatureModalData(featureBookId, booksPage.getContent());
            model.addAttribute("showFeatureModal", featureModalData.showModal());
            model.addAttribute("featureBook", featureModalData.featureBook());
            model.addAttribute("featureModalBooks", featureModalData.books());
            model.addAttribute("featureRows", featureModalData.featureRows());

            BookService.ImageModalData imageModalData = bookService.buildImageModalData(imageBookId, booksPage.getContent());
            model.addAttribute("showImageModal", imageModalData.showModal());
            model.addAttribute("imageBook", imageModalData.imageBook());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            logger.error("Error fetching books", ex);
        }
        return "books";
    }

    /**
     * Searches for books using the provided search request.
     *
     * @param request search request data
     * @return search result or error response
     */
    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<?> searchBooks(@Valid @RequestBody SearchRequestDTO request) {
        try {
            SearchResultDTO result = searchService.search(request);
            return ok(result);
        } catch (Exception ex) {
            logger.error("Error searching books", ex);
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
