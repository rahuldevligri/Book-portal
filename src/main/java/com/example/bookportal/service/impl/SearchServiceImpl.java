package com.example.bookportal.service.impl;

import com.example.bookportal.dto.BookSummaryDTO;
import com.example.bookportal.dto.SearchRequestDTO;
import com.example.bookportal.dto.SearchResultDTO;
import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.repository.*;
import com.example.bookportal.repository.projection.SearchBookSummaryProjection;
import com.example.bookportal.service.SearchService;
import com.example.bookportal.specification.BookSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Set<String> ALLOWED_MATCH_TYPES = Set.of("exact", "start", "contains");
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title");
    private static final String GENERIC_BOOK_THUMBNAIL = "/thumb/genericBook.jpg";

    private final BookRepository bookRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookPublisherRepository bookPublisherRepository;
    private final PublisherRepository publisherRepository;

    public SearchServiceImpl(BookRepository bookRepository,
                             BookAuthorRepository bookAuthorRepository,
                             AuthorRepository authorRepository,
                             CategoryRepository categoryRepository,
                             BookPublisherRepository bookPublisherRepository,
                             PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookPublisherRepository = bookPublisherRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResultDTO search(SearchRequestDTO request) {

        String q = request.getQuery();
        String type = normalizeType(request.getType());
        String matchType = normalizeMatchType(request.getMatchType());

        int pageIndex = Math.max(request.getPage(), 0);
        int pageSize = Math.min(Math.max(request.getSize(), 1), 100);

        Pageable pageable = PageRequest.of(pageIndex, pageSize, buildSort(request));

        String baseQuery = q == null ? "" : q.trim().toLowerCase();
        boolean exact = "exact".equalsIgnoreCase(matchType);
        String query = buildQueryPattern(baseQuery, matchType);

        // ================= AUTHOR =================
        if ("AUTHOR".equalsIgnoreCase(type)) {

            Page<SearchBookSummaryProjection> page =
                    bookRepository.searchAuthor(query, exact, pageable);

            List<String> matchingAuthors = null;

            if (!exact) {
                matchingAuthors = authorRepository.findDistinctAuthorNamesForSearch(query);
            }

            return buildResult(page, matchingAuthors, null);
        }

        // ================= PUBLISHER =================
        if ("PUBLISHER".equalsIgnoreCase(type)) {

            Page<SearchBookSummaryProjection> page =
                    bookRepository.searchPublisher(query, exact, pageable);

            List<String> matchingPublishers = null;

            if (!exact) {
                matchingPublishers = publisherRepository.findDistinctPublisherNamesForSearch(query);
            }

            return buildResult(page, null, matchingPublishers);
        }

        // ================= FALLBACK =================
        Specification<BookEntity> spec = BookSpecification.active()
                .and(BookSpecification.containsText(q, type, matchType));

        Page<BookEntity> page = bookRepository.findAll(spec, pageable);
        List<BookSummaryDTO> items = toSummaryBatch(page.getContent());

        return new SearchResultDTO(
                items,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    // ================= COMMON BUILDER =================

    private SearchResultDTO buildResult(Page<SearchBookSummaryProjection> page,
                                        List<String> authors,
                                        List<String> publishers) {

        List<BookSummaryDTO> items = page.getContent()
                .stream()
                .map(this::toSummary)
                .toList();

        return new SearchResultDTO(
                items,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                authors,
                publishers
        );
    }

    // ================= DTO MAPPING =================

    private BookSummaryDTO toSummary(SearchBookSummaryProjection p) {
        String thumbUrl = toThumbnailUrl(p.getThumbnailPath(), p.getImagePath());
        String fullUrl = toFullImageUrl(p.getImagePath(), p.getThumbnailPath());
        return new BookSummaryDTO(
                p.getId(),
                p.getTitle(),
                safe(p.getAuthorName(), "Unknown Author"),
                safe(p.getCategoryName(), "N/A"),
                safe(p.getPublisherName(), "Unknown Publisher"),
                null,
                thumbUrl,
                fullUrl
        );
    }

    private List<BookSummaryDTO> toSummaryBatch(List<BookEntity> books) {

        if (books == null || books.isEmpty()) return List.of();

        List<Long> bookIds = books.stream().map(BookEntity::getId).toList();
        Set<Long> categoryIds = books.stream()
                .map(BookEntity::getBookCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> categoryNames = categoryRepository
                .findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> safe(c.getCategory(), "N/A")));

        Map<Long, Long> firstAuthor = new LinkedHashMap<>();
        bookAuthorRepository.findByBookIdInOrderByIdAsc(bookIds)
                .forEach(m -> firstAuthor.putIfAbsent(m.getBookId(), m.getAuthorId()));

        Map<Long, String> authorNames = authorRepository
                .findAllById(new HashSet<>(firstAuthor.values()))
                .stream()
                .collect(Collectors.toMap(a -> a.getId(),
                        a -> formatPersonName(a.getFirstName(), a.getLastName())));

        Map<Long, Long> firstPublisher = new LinkedHashMap<>();
        bookPublisherRepository.findByBookIdInOrderByIdAsc(bookIds)
                .forEach(m -> firstPublisher.putIfAbsent(m.getBookId(), m.getPublisherId()));

        Map<Long, String> publisherNames = publisherRepository
                .findAllById(new HashSet<>(firstPublisher.values()))
                .stream()
                .collect(Collectors.toMap(p -> p.getId(), p -> safe(p.getName(), "")));

        return books.stream().map(book -> new BookSummaryDTO(
                book.getId(),
                book.getTitle(),
                safe(authorNames.get(firstAuthor.get(book.getId())), "Unknown Author"),
                safe(categoryNames.get(book.getBookCategoryId()), "N/A"),
                safe(publisherNames.get(firstPublisher.get(book.getId())), "Unknown Publisher"),
                null,
                book.getThumbnailUrl(),
                book.getFullImageUrl()
        )).toList();
    }

    // ================= HELPERS =================

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    private String formatPersonName(String fn, String ln) {
        return ((fn == null ? "" : fn.trim()) + " " + (ln == null ? "" : ln.trim())).trim();
    }

    private String toThumbnailUrl(String thumbnailPath, String fullImagePath) {
        String thumb = normalizeUrl(thumbnailPath, "/thumb/");
        if (thumb != null) {
            return thumb;
        }
        String full = normalizeUrl(fullImagePath, "/images/");
        return full != null ? full : GENERIC_BOOK_THUMBNAIL;
    }

    private String toFullImageUrl(String fullImagePath, String thumbnailPath) {
        String full = normalizeUrl(fullImagePath, "/images/");
        if (full != null) {
            return full;
        }
        String thumb = normalizeUrl(thumbnailPath, "/thumb/");
        return thumb != null ? thumb : GENERIC_BOOK_THUMBNAIL;
    }

    private String normalizeUrl(String rawPath, String defaultPrefix) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }
        String path = rawPath.trim().replace("\\", "/");
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("data:")) {
            return path;
        }
        if (path.startsWith("/")) {
            return path;
        }
        if (path.contains("/")) {
            return "/" + path;
        }
        return defaultPrefix + path;
    }

    private String normalizeType(String type) {
        return type == null ? "ALL" : type.trim().toUpperCase();
    }

    private String normalizeMatchType(String matchType) {
        if (matchType == null || matchType.isBlank()) {
            return "contains";
        }
        String normalized = matchType.trim().toLowerCase();
        return ALLOWED_MATCH_TYPES.contains(normalized) ? normalized : "contains";
    }

    private String buildQueryPattern(String baseQuery, String matchType) {
        if ("exact".equalsIgnoreCase(matchType)) {
            return baseQuery;
        }
        if ("start".equalsIgnoreCase(matchType)) {
            return baseQuery + "%";
        }
        return "%" + baseQuery + "%";
    }

    private Sort buildSort(SearchRequestDTO request) {
        String requestedSort = request.getSort();
        String sortField = (requestedSort == null || requestedSort.isBlank())
                ? "title"
                : requestedSort.trim();
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "title";
        }
        Sort.Direction direction = Sort.Direction.fromOptionalString(request.getDirection())
                .orElse(Sort.Direction.ASC);
        return Sort.by(direction, sortField);
    }

}
