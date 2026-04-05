package com.example.bookportal.service;

import com.example.bookportal.dto.BookSummaryDTO;
import com.example.bookportal.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing books and related operations.
 */
public interface BookService {
    /**
     * Small view model for compare-table rows.
     */
    record CompareFeatureRow(String featureName, List<String> values) {
    }

    /**
     * Comparison result for the books page.
     */
    record CompareData(List<Long> selectedBookIds,
                       List<BookEntity> books,
                       List<CompareFeatureRow> featureRows,
                       String errorKey) {
        public boolean showModal() {
            return errorKey == null && books != null && !books.isEmpty();
        }
    }

    /**
     * Feature modal data for a single selected book.
     */
    record FeatureModalData(BookEntity featureBook,
                            List<BookEntity> books,
                            List<CompareFeatureRow> featureRows) {
        public boolean showModal() {
            return featureBook != null;
        }
    }

    /**
     * Modal data for full-size book image preview.
     */
    record ImageModalData(BookEntity imageBook) {
        public boolean showModal() {
            return imageBook != null;
        }
    }

    /**
     * Retrieves paginated books by author and category.
     *
     * @param authorId   author ID
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of books
     */
    Page<BookEntity> getBooksByAuthorAndCategory(Long authorId, Long categoryId, Pageable pageable);

    /**
     * Retrieves paginated books by publisher and category.
     *
     * @param publisherId publisher ID
     * @param categoryId  category ID
     * @param pageable    pagination information
     * @return page of books
     */
    Page<BookEntity> getBooksByPublisherAndCategory(Long publisherId, Long categoryId, Pageable pageable);

    /**
     * Retrieves paginated books by category.
     *
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of books
     */
    Page<BookEntity> getBooksByCategory(Long categoryId, Pageable pageable);

    /**
     * Retrieves paginated books by author.
     *
     * @param authorId author ID
     * @param pageable pagination information
     * @return page of books
     */
    Page<BookEntity> getBooksByAuthor(Long authorId, Pageable pageable);

    /**
     * Retrieves paginated books by publisher.
     *
     * @param publisherId publisher ID
     * @param pageable    pagination information
     * @return page of books
     */
    Page<BookEntity> getBooksByPublisher(Long publisherId, Pageable pageable);

    /**
     * Resolves the filtered books page for the books screen.
     *
     * @param authorId author filter
     * @param publisherId publisher filter
     * @param categoryId category filter
     * @param pageable normalized paging/sorting
     * @return page of books for the requested filter combination
     */
    Page<BookEntity> getBooksPage(Long authorId, Long publisherId, Long categoryId, Pageable pageable, String query, String matchType);

    /**
     * Retrieves multiple books by their IDs in the same order as requested.
     * Missing/invalid IDs are ignored.
     *
     * @param ids book IDs
     * @return list of books
     */
    List<BookEntity> getBooksByIds(List<Long> ids);

    /**
     * Builds comparison data for the books page.
     *
     * @param requestedBookIds raw requested IDs
     * @param maxComparisonBooks max allowed books in a comparison
     * @param compareRequested whether the user explicitly requested compare
     * @return comparison data and validation state
     */
    CompareData buildCompareData(List<Long> requestedBookIds, int maxComparisonBooks, boolean compareRequested);

    /**
     * Builds feature modal data for a selected book on the books page.
     *
     * @param featureBookId selected book ID
     * @param pageBooks books currently visible in the page
     * @return modal data for single-book feature display
     */
    FeatureModalData buildFeatureModalData(Long featureBookId, List<BookEntity> pageBooks);

    /**
     * Builds image modal data for a selected book on the books page.
     *
     * @param imageBookId selected book ID
     * @param pageBooks books currently visible in the page
     * @return modal data for full image display
     */
    ImageModalData buildImageModalData(Long imageBookId, List<BookEntity> pageBooks);

    /**
     * Resolves a safe return URL for books page back navigation.
     *
     * @param returnTo requested return URL
     * @param authorId author filter
     * @param publisherId publisher filter
     * @param categoryId category filter
     * @return safe back URL
     */
    String resolveBooksBackUrl(String returnTo, Long authorId, Long publisherId, Long categoryId);

    /**
     * Builds display summaries for a list of books using batched lookups.
     *
     * @param books books to summarize
     * @return list of summaries
     */
    List<BookSummaryDTO> buildBookSummaries(List<BookEntity> books);

}
