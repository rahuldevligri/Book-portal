package com.example.bookportal.service.impl;

import com.example.bookportal.dto.BookSummaryDTO;
import com.example.bookportal.dto.BookFeatureDTO;
import com.example.bookportal.entity.BookAuthorEntity;
import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.entity.BookPublisherEntity;
import com.example.bookportal.repository.BookAuthorRepository;
import com.example.bookportal.repository.BookPublisherRepository;
import com.example.bookportal.repository.AuthorRepository;
import com.example.bookportal.repository.CategoryRepository;
import com.example.bookportal.repository.PublisherRepository;
import com.example.bookportal.repository.BookRepository;
import com.example.bookportal.service.BookFeatureService;
import com.example.bookportal.service.BookService;
import com.example.bookportal.specification.BookSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Locale;
import java.util.stream.Collectors;

@Service("bookService")
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private static final String UNKNOWN_AUTHOR = "Unknown Author";
    private static final String UNKNOWN_PUBLISHER = "Unknown Publisher";
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookAuthorRepository bookAuthorRepository;
    @Autowired
    private BookPublisherRepository bookPublisherRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private BookFeatureService bookFeatureService;

    /**
     * Retrieves multiple books by their IDs in the same order as requested.
     * Missing/invalid IDs are ignored.
     *
     * @param ids book IDs
     * @return list of books
     */
    @Override
    public List<BookEntity> getBooksByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        // keep input order + ignore invalid values
        List<Long> requested = ids.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());
        if (requested.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, BookEntity> byId = new LinkedHashMap<>();
        bookRepository.findAllById(requested).forEach(b -> byId.put(b.getId(), b));

        List<BookEntity> ordered = new ArrayList<>(byId.size());
        for (Long id : requested) {
            BookEntity b = byId.get(id);
            if (b != null) {
                ordered.add(b);
            }
        }
        return ordered;
    }

    @Override
    public CompareData buildCompareData(List<Long> requestedBookIds, int maxComparisonBooks, boolean compareRequested) {
        List<Long> selectedBookIds = requestedBookIds == null
                ? List.of()
                : requestedBookIds.stream()
                        .filter(id -> id != null && id > 0)
                        .distinct()
                        .collect(Collectors.toList());

        boolean fewerThanTwoSelected = selectedBookIds.size() < 2;
        boolean tooManySelected = selectedBookIds.size() > maxComparisonBooks;

        String errorKey = null;
        if (compareRequested && fewerThanTwoSelected) {
            errorKey = "compare.select.one";
        } else if (compareRequested && tooManySelected) {
            errorKey = "compare.max.five";
        }

        if (fewerThanTwoSelected || tooManySelected) {
            return new CompareData(selectedBookIds, List.of(), List.of(), errorKey);
        }

        List<Long> safeBookIds = selectedBookIds;
        if (safeBookIds.isEmpty()) {
            return new CompareData(List.of(), List.of(), List.of(), errorKey);
        }

        List<BookEntity> booksToCompare = getBooksByIds(safeBookIds);
        if (booksToCompare.isEmpty()) {
            return new CompareData(List.of(), List.of(), List.of(), errorKey);
        }

        List<Long> validBookIds = booksToCompare.stream()
                .map(BookEntity::getId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toList());

        Map<Long, List<com.example.bookportal.dto.BookFeatureDTO>> booksFeatures = bookFeatureService.getBooksFeatures(validBookIds);
        Map<Long, Map<String, String>> featureMatrix = new LinkedHashMap<>();
        for (Long bookId : validBookIds) {
            featureMatrix.put(bookId, new LinkedHashMap<>());
        }

        booksFeatures.forEach((bookId, features) -> {
            if (features == null) {
                featureMatrix.putIfAbsent(bookId, Collections.emptyMap());
                return;
            }
            Map<String, String> perBook = featureMatrix.computeIfAbsent(bookId, ignored -> new LinkedHashMap<>());
            features.forEach(feature -> {
                if (feature == null || feature.getFeatureName() == null) {
                    return;
                }
                String featureName = feature.getFeatureName().trim();
                if (featureName.isEmpty()) {
                    return;
                }
                String featureValue = feature.getValue();
                featureValue = featureValue == null ? null : featureValue.trim();
                String existing = perBook.get(featureName);
                boolean existingHasValue = existing != null && !existing.isBlank();
                boolean newHasValue = featureValue != null && !featureValue.isBlank();
                if (!existingHasValue && newHasValue) {
                    perBook.put(featureName, featureValue);
                } else if (!perBook.containsKey(featureName)) {
                    perBook.put(featureName, featureValue);
                }
            });
        });

        Set<String> allFeatureNames = new LinkedHashSet<>();
        featureMatrix.values().forEach(bookFeatures -> {
            if (bookFeatures != null) {
                allFeatureNames.addAll(bookFeatures.keySet());
            }
        });

        List<CompareFeatureRow> featureRows = new ArrayList<>();
        for (String featureName : allFeatureNames) {
            List<String> values = new ArrayList<>();
            for (BookEntity book : booksToCompare) {
                Map<String, String> perBook = featureMatrix.get(book.getId());
                String value = perBook != null ? perBook.get(featureName) : null;
                values.add((value != null && !value.isBlank()) ? value : "-");
            }
            featureRows.add(new CompareFeatureRow(featureName, values));
        }

        return new CompareData(validBookIds, booksToCompare, featureRows, errorKey);
    }

    @Override
    public FeatureModalData buildFeatureModalData(Long featureBookId, List<BookEntity> pageBooks) {
        BookEntity featureBook = resolveFeatureBook(featureBookId, pageBooks);
        List<BookEntity> featureModalBooks = featureBook != null ? List.of(featureBook) : List.of();
        List<CompareFeatureRow> featureRows = buildSingleBookFeatureRows(featureBook);
        return new FeatureModalData(featureBook, featureModalBooks, featureRows);
    }

    @Override
    public ImageModalData buildImageModalData(Long imageBookId, List<BookEntity> pageBooks) {
        BookEntity imageBook = resolveImageBook(imageBookId, pageBooks);
        return new ImageModalData(imageBook);
    }

    @Override
    public String resolveBooksBackUrl(String returnTo, Long authorId, Long publisherId, Long categoryId) {
        String fallback = "/dashboard";
        if (authorId != null && authorId > 0) {
            fallback = "/authors/" + authorId;
        } else if (publisherId != null && publisherId > 0) {
            fallback = "/publishers/" + publisherId;
        } else if (categoryId != null && categoryId > 0) {
            fallback = "/categories/" + categoryId;
        }
        return resolveReturnTo(returnTo, fallback);
    }

    /**
     * Builds display summaries for a list of books using batched lookups.
     *
     * @param books books to summarize
     * @return list of summaries
     */
    @Override
    public List<BookSummaryDTO> buildBookSummaries(List<BookEntity> books) {
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> bookIds = books.stream().map(BookEntity::getId).toList();
        Set<Long> categoryIds = books.stream()
                .map(BookEntity::getBookCategoryId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        Map<Long, String> categoryById = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> c.getCategory() != null ? c.getCategory() : "N/A"));

        Map<Long, Long> firstAuthorByBook = new LinkedHashMap<>();
        for (BookAuthorEntity mapping : bookAuthorRepository.findByBookIdInOrderByIdAsc(bookIds)) {
            firstAuthorByBook.putIfAbsent(mapping.getBookId(), mapping.getAuthorId());
        }
        Set<Long> authorIds = firstAuthorByBook.values().stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        Map<Long, String> authorById = new HashMap<>();
        authorRepository.findAllById(authorIds).forEach(author -> {
            String name = ((author.getFirstName() == null ? "" : author.getFirstName().trim())
                    + " "
                    + (author.getLastName() == null ? "" : author.getLastName().trim())).trim();
            authorById.put(author.getId(), name.isBlank() ? UNKNOWN_AUTHOR : name);
        });

        Map<Long, Long> firstPublisherByBook = new LinkedHashMap<>();
        for (BookPublisherEntity mapping : bookPublisherRepository.findByBookIdInOrderByIdAsc(bookIds)) {
            firstPublisherByBook.putIfAbsent(mapping.getBookId(), mapping.getPublisherId());
        }
        Set<Long> publisherIds = firstPublisherByBook.values().stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        Map<Long, String> publisherById = new HashMap<>();
        publisherRepository.findAllById(publisherIds).forEach(publisher -> {
            String name = publisher.getName() != null ? publisher.getName().trim() : "";
            publisherById.put(publisher.getId(), name.isBlank() ? UNKNOWN_PUBLISHER : name);
        });

        return books.stream().map(book -> {
            String categoryName = categoryById.getOrDefault(book.getBookCategoryId(), "N/A");

            Long authorId = firstAuthorByBook.get(book.getId());
            String authorName = authorId != null
                    ? authorById.getOrDefault(authorId, UNKNOWN_AUTHOR)
                    : UNKNOWN_AUTHOR;

            Long publisherId = firstPublisherByBook.get(book.getId());
            String publisherName = publisherId != null
                    ? publisherById.getOrDefault(publisherId, UNKNOWN_PUBLISHER)
                    : UNKNOWN_PUBLISHER;

            return new BookSummaryDTO(
                    book.getId(),
                    book.getTitle(),
                    authorName,
                    categoryName,
                    publisherName,
                    null,
                    book.getThumbnailUrl(),
                    book.getFullImageUrl());
        }).toList();
    }

    /**
     * Retrieves paginated books by author and category.
     *
     * @param authorId   author ID
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByAuthorAndCategory(Long authorId, Long categoryId, Pageable pageable) {
        logger.info("Fetching books by authorId: {}, categoryId: {} with pagination", authorId, categoryId);
        return bookRepository.findByAuthorAndCategory(authorId, categoryId, pageable);
    }

    /**
     * Retrieves paginated books by publisher and category.
     *
     * @param publisherId publisher ID
     * @param categoryId  category ID
     * @param pageable    pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByPublisherAndCategory(Long publisherId, Long categoryId, Pageable pageable) {
        logger.info("Fetching books by publisherId: {}, categoryId: {} with pagination", publisherId, categoryId);
        return bookRepository.findByPublisherAndCategory(publisherId, categoryId, pageable);
    }

    /**
     * Retrieves paginated books by category.
     *
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByCategory(Long categoryId, Pageable pageable) {
        logger.info("Fetching books by categoryId: {} with pagination", categoryId);
        return bookRepository.findByBookCategoryId(categoryId, pageable);
    }

    /**
     * Retrieves paginated books by author.
     *
     * @param authorId author ID
     * @param pageable pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByAuthor(Long authorId, Pageable pageable) {
        logger.info("Fetching books by authorId: {} with pagination", authorId);
        return bookRepository.findByAuthor(authorId, pageable);
    }

    /**
     * Retrieves paginated books by publisher.
     *
     * @param publisherId publisher ID
     * @param pageable    pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByPublisher(Long publisherId, Pageable pageable) {
        logger.info("Fetching books by publisherId: {} with pagination", publisherId);
        return bookRepository.findByPublisher(publisherId, pageable);
    }

    @Override
    public Page<BookEntity> getBooksPage(Long authorId, Long publisherId, Long categoryId, Pageable pageable, String query, String matchType) {
        String normalizedQuery = query == null ? "" : query.trim();
        if (!normalizedQuery.isEmpty()) {
            Specification<BookEntity> titleSearchSpec = BookSpecification.active()
                    .and(BookSpecification.containsText(normalizedQuery, "TITLE", normalizeMatchType(matchType)));
            return bookRepository.findAll(titleSearchSpec, pageable);
        }

        if (authorId != null && categoryId != null) {
            return getBooksByAuthorAndCategory(authorId, categoryId, pageable);
        }
        if (publisherId != null && categoryId != null) {
            return getBooksByPublisherAndCategory(publisherId, categoryId, pageable);
        }
        if (authorId != null) {
            return getBooksByAuthor(authorId, pageable);
        }
        if (publisherId != null) {
            return getBooksByPublisher(publisherId, pageable);
        }
        if (categoryId != null) {
            return getBooksByCategory(categoryId, pageable);
        }
        return Page.empty(pageable);
    }

    private String normalizeMatchType(String matchType) {
        if (matchType == null || matchType.isBlank()) {
            return "contains";
        }
        String normalized = matchType.trim().toLowerCase(Locale.ROOT);
        if ("exact".equals(normalized) || "start".equals(normalized) || "contains".equals(normalized)) {
            return normalized;
        }
        return "contains";
    }

    private BookEntity resolveFeatureBook(Long featureBookId, List<BookEntity> pageBooks) {
        if (featureBookId == null || featureBookId <= 0) {
            return null;
        }

        if (pageBooks != null) {
            for (BookEntity book : pageBooks) {
                if (book != null && featureBookId.equals(book.getId())) {
                    return book;
                }
            }
        }

        return getBooksByIds(List.of(featureBookId)).stream().findFirst().orElse(null);
    }

    private BookEntity resolveImageBook(Long imageBookId, List<BookEntity> pageBooks) {
        if (imageBookId == null || imageBookId <= 0) {
            return null;
        }

        if (pageBooks != null) {
            for (BookEntity book : pageBooks) {
                if (book != null && imageBookId.equals(book.getId())) {
                    return book;
                }
            }
        }

        return getBooksByIds(List.of(imageBookId)).stream().findFirst().orElse(null);
    }

    private List<CompareFeatureRow> buildSingleBookFeatureRows(BookEntity featureBook) {
        if (featureBook == null || featureBook.getId() == null) {
            return List.of();
        }

        List<BookFeatureDTO> features = bookFeatureService
                .getBooksFeatures(List.of(featureBook.getId()))
                .getOrDefault(featureBook.getId(), List.of());

        LinkedHashMap<String, String> byName = new LinkedHashMap<>();
        for (BookFeatureDTO feature : features) {
            if (feature == null || feature.getFeatureName() == null) {
                continue;
            }
            String featureName = feature.getFeatureName().trim();
            if (featureName.isEmpty()) {
                continue;
            }
            String featureValue = feature.getValue();
            featureValue = featureValue == null ? null : featureValue.trim();

            String existing = byName.get(featureName);
            boolean existingHasValue = existing != null && !existing.isBlank();
            boolean newHasValue = featureValue != null && !featureValue.isBlank();
            if (!existingHasValue && newHasValue) {
                byName.put(featureName, featureValue);
            } else if (!byName.containsKey(featureName)) {
                byName.put(featureName, featureValue);
            }
        }

        List<CompareFeatureRow> rows = new ArrayList<>();
        byName.forEach((featureName, value) -> rows.add(
                new CompareFeatureRow(
                        featureName,
                        List.of(value != null && !value.isBlank() ? value : "-"))));
        return rows;
    }

    private String resolveReturnTo(String returnTo, String fallback) {
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
