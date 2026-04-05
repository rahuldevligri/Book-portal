package com.example.bookportal.service.impl;

import com.example.bookportal.dto.AuthorDTO;
import com.example.bookportal.entity.AuthorEntity;
import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.repository.AuthorRepository;
import com.example.bookportal.repository.BookRepository;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;
import com.example.bookportal.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

@Service
public class AuthorServiceImpl implements AuthorService {
    /**
     * Deletes authors by IDs.
     *
     * @param authorIds list of author IDs
     */
    @Override
    public void deleteAuthors(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return;
        }
        for (Long authorId : authorIds) {
            if (authorId == null || authorId <= 0) {
                throw new ValidationException("Invalid author ID");
            }
        }
        authorRepository.deleteAllByIdInBatch(authorIds);
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    /**
     * Retrieves paginated authors.
     *
     * @param pageable pagination information
     * @return page of authors
     */
    @Override
    public Page<AuthorEntity> getAuthorsPage(Pageable pageable) {
        logger.info("Fetching authors page: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return authorRepository.findAll(pageable);
    }

    @Override
    public Page<AuthorEntity> searchAuthors(String query, String matchType, Pageable pageable) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return getAuthorsPage(pageable);
        }

        List<AuthorEntity> matches = authorRepository.findMatchingForSearch(buildLikePattern(normalized, matchType));
        if ("exact".equals(normalizeMatchType(matchType))) {
            matches = matches.stream().filter(a -> isAuthorExactMatch(a, normalized)).toList();
        }

        int start = Math.min((int) pageable.getOffset(), matches.size());
        int end = Math.min(start + pageable.getPageSize(), matches.size());
        return new PageImpl<>(matches.subList(start, end), pageable, matches.size());
    }

    /**
     * Retrieves an author by ID.
     *
     * @param authorId ID of the author
     * @return author entity
     */
    @Override
    public AuthorEntity getAuthorById(Long authorId) {
        if (authorId == null || authorId <= 0) {
            throw new ValidationException("Invalid author ID");
        }
        logger.info("Fetching author by id: {}", authorId);
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("AuthorEntity not found"));
    }

    /**
     * Retrieves paginated books for an author.
     *
     * @param authorId ID of the author
     * @param pageable pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByAuthor(Long authorId, Pageable pageable) {
        logger.info("Fetching books by author id: {} with pagination", authorId);
        return bookRepository.findByAuthor(authorId, pageable);
    }

    /**
     * Creates a new author.
     *
     * @param author author entity
     * @return persisted author
     */
    @Override
    public AuthorEntity createAuthor(AuthorEntity author) {
        validateAuthor(author);
        return authorRepository.save(author);
    }

    /**
     * Updates an existing author.
     *
     * @param id     author id
     * @param author updated data
     * @return updated author
     */
    @Override
    public AuthorEntity updateAuthor(Long id, AuthorEntity author) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid author ID");
        }
        AuthorEntity existing = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AuthorEntity not found"));
        validateAuthor(author);

        boolean changed = false;
        if (!equals(existing.getFirstName(), author.getFirstName()))
            changed = true;
        if (!equals(existing.getLastName(), author.getLastName()))
            changed = true;
        if (!equals(existing.getEmail(), author.getEmail()))
            changed = true;

        if (!changed) {
            // No changes, skip save
            return existing;
        }

        existing.setFirstName(author.getFirstName());
        existing.setLastName(author.getLastName());
        existing.setEmail(author.getEmail());
        return authorRepository.save(existing);
    }

    @Override
    public AuthorEntity fromDto(AuthorDTO dto) {
        if (dto == null) {
            return null;
        }
        AuthorEntity author = new AuthorEntity();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setEmail(dto.getEmail());
        return author;
    }

    @Override
    public AuthorDTO toDto(AuthorEntity entity) {
        if (entity == null) {
            return null;
        }
        AuthorDTO dto = new AuthorDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    @Override
    public boolean hasChanges(AuthorEntity existing, AuthorDTO dto) {
        if (existing == null || dto == null) {
            return true;
        }
        return !equalsTrim(existing.getFirstName(), dto.getFirstName())
                || !equalsTrim(existing.getLastName(), dto.getLastName())
                || !equalsTrim(existing.getEmail(), dto.getEmail());
    }

    /**
     * Validates the given author entity.
     *
     * @param author the author entity to validate
     * @throws ValidationException if validation fails
     */
    private void validateAuthor(AuthorEntity author) {
        if (author == null) {
            throw new ValidationException("AuthorEntity cannot be null");
        }
        if (author.getFirstName() == null || author.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (author.getLastName() == null || author.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
        if (author.getEmail() == null
                || !author.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Valid email is required");
        }
    }

    /**
     * Compares two strings for equality, handling nulls.
     *
     * @param a first string
     * @param b second string
     * @return true if both are equal or both null
     */
    private boolean equals(String a, String b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private boolean equalsTrim(String left, String right) {
        String a = left == null ? "" : left.trim();
        String b = right == null ? "" : right.trim();
        return a.equalsIgnoreCase(b);
    }

    @Override
    public List<CategoryBookCountProjection> getCategoryWiseBooks(Long authorId) {
        logger.info("Fetching category-wise book count for author id: {}", authorId);
        return bookRepository.findCategoryWiseBookCountByAuthor(authorId);
    }

    private String buildLikePattern(String text, String matchType) {
        String mode = normalizeMatchType(matchType);
        if ("exact".equals(mode)) {
            return text;
        }
        if ("start".equals(mode)) {
            return text + "%";
        }
        return "%" + text + "%";
    }

    private String normalizeMatchType(String matchType) {
        if (matchType == null || matchType.isBlank()) {
            return "contains";
        }
        String mode = matchType.trim().toLowerCase(Locale.ROOT);
        if ("exact".equals(mode) || "start".equals(mode) || "contains".equals(mode)) {
            return mode;
        }
        return "contains";
    }

    private boolean isAuthorExactMatch(AuthorEntity author, String normalizedQuery) {
        if (author == null) {
            return false;
        }
        String first = author.getFirstName() == null ? "" : author.getFirstName().trim().toLowerCase(Locale.ROOT);
        String last = author.getLastName() == null ? "" : author.getLastName().trim().toLowerCase(Locale.ROOT);
        String fullWithSpace = (first + " " + last).trim();
        String fullNoSpace = (first + last).trim();
        return fullWithSpace.equals(normalizedQuery)
                || fullNoSpace.equals(normalizedQuery);
    }
}
