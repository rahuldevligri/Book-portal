package com.example.bookportal.service.impl;

import com.example.bookportal.dto.PublisherDTO;
import com.example.bookportal.entity.BookEntity;
import com.example.bookportal.entity.PublisherEntity;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.repository.BookRepository;
import com.example.bookportal.repository.PublisherRepository;
import com.example.bookportal.repository.projection.CategoryBookCountProjection;
import com.example.bookportal.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

@Service
public class PublisherServiceImpl implements PublisherService {
    /**
     * Retrieves paginated publishers.
     *
     * @param pageable pagination information
     * @return page of publishers
     */
    @Override
    public Page<PublisherEntity> getPublishersPage(Pageable pageable) {
        logger.info("Fetching publishers page: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return publisherRepository.findAll(pageable);
    }

    @Override
    public Page<PublisherEntity> searchPublishers(String query, String matchType, Pageable pageable) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return getPublishersPage(pageable);
        }

        List<PublisherEntity> matches = publisherRepository.findMatchingForSearch(buildLikePattern(normalized, matchType));
        if ("exact".equals(normalizeMatchType(matchType))) {
            matches = matches.stream().filter(p -> isPublisherExactMatch(p, normalized)).toList();
        }
        int start = Math.min((int) pageable.getOffset(), matches.size());
        int end = Math.min(start + pageable.getPageSize(), matches.size());
        return new PageImpl<>(matches.subList(start, end), pageable, matches.size());
    }

    @Override
    public List<PublisherEntity> getAllPublishers() {
        logger.info("Fetching all publishers for selection controls");
        return publisherRepository.findAll();
    }

    private static final Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MessageSource messageSource;

    /**
     * Retrieves paginated publishers/distributors by entity type id.
     *
     * @param entityTypeId entity type id
     * @param pageable     pagination information
     * @return page of matching publishers
     */
    @Override
    public Page<PublisherEntity> getPublishersByType(Long entityTypeId, Pageable pageable) {
        if (entityTypeId == null || entityTypeId <= 0) {
            throw new ValidationException("Invalid entity type id");
        }
        logger.info("Fetching publishers by entity type id: {} page={}, size={}",
                entityTypeId, pageable.getPageNumber(), pageable.getPageSize());
        return publisherRepository.findByEntityTypeId(entityTypeId, pageable);
    }

    /**
     * Retrieves a publisher by ID.
     *
     * @param id publisher ID
     * @return publisher entity
     */
    @Override
    public PublisherEntity getPublisherById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid publisher ID");
        }
        logger.info("Fetching publisher with id: {}", id);
        return publisherRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("PublisherEntity not found with id: {}", id);
                    return new RuntimeException("PublisherEntity not found with id: " + id);
                });
    }

    /**
     * Retrieves category-wise book counts for a publisher.
     *
     * @param id publisher ID
     * @return list of category book count projections
     */
    @Override
    public List<CategoryBookCountProjection> getCategoryWiseBooks(Long id) {
        logger.info("Fetching category wise book count for publisher id: {}", id);
        return bookRepository.findCategoryWiseBookCountByPublisher(id);
    }

    /**
     * Retrieves the total book count for a publisher.
     *
     * @param id publisher ID
     * @return number of books
     */
    @Override
    public long getPublisherBookCount(Long id) {
        logger.info("Fetching book count for publisher id: {}", id);
        return bookRepository.countByPublisher(id);
    }

    /**
     * Retrieves paginated books for a publisher.
     *
     * @param publisherId publisher ID
     * @param pageable    pagination information
     * @return page of books
     */
    @Override
    public Page<BookEntity> getBooksByPublisher(Long publisherId, Pageable pageable) {
        logger.info("Fetching active books for publisher id: {} with pagination", publisherId);
        return bookRepository.findByPublisher(publisherId, pageable);
    }

    @Override
    public PublisherEntity createPublisher(PublisherEntity publisher, Long entityTypeId) {
        validatePublisher(publisher);
        publisher.setEntityTypeId(entityTypeId);
        return publisherRepository.save(publisher);
    }

    @Override
    public PublisherEntity updatePublisher(Long id, PublisherEntity publisher) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid publisher ID");
        }
        PublisherEntity existing = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PublisherEntity not found with id: " + id));
        validatePublisher(publisher);

        boolean changed = false;
        if (!equals(existing.getName(), publisher.getName()))
            changed = true;
        if (!equals(existing.getAddress(), publisher.getAddress()))
            changed = true;
        if (!equals(existing.getTelephone(), publisher.getTelephone()))
            changed = true;
        if (!equals(existing.getFax(), publisher.getFax()))
            changed = true;
        if (!equals(existing.getEmail(), publisher.getEmail()))
            changed = true;
        if (!equals(existing.getWebSite(), publisher.getWebSite()))
            changed = true;

        if (!changed) {
            // No changes, skip save
            return existing;
        }

        existing.setName(publisher.getName());
        existing.setAddress(publisher.getAddress());
        existing.setTelephone(publisher.getTelephone());
        existing.setFax(publisher.getFax());
        existing.setEmail(publisher.getEmail());
        existing.setWebSite(publisher.getWebSite());
        return publisherRepository.save(existing);
    }

    @Override
    public PublisherEntity fromDto(PublisherDTO dto) {
        if (dto == null) {
            return null;
        }
        PublisherEntity entity = new PublisherEntity();
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setTelephone(dto.getTelephone());
        entity.setFax(dto.getFax());
        entity.setEmail(dto.getEmail());
        entity.setWebSite(dto.getWebSite());
        return entity;
    }

    @Override
    public PublisherDTO toDto(PublisherEntity entity) {
        if (entity == null) {
            return null;
        }
        PublisherDTO dto = new PublisherDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setTelephone(entity.getTelephone());
        dto.setFax(entity.getFax());
        dto.setEmail(entity.getEmail());
        dto.setWebSite(entity.getWebSite());
        return dto;
    }

    @Override
    public boolean hasChanges(PublisherEntity existing, PublisherDTO dto) {
        if (existing == null || dto == null) {
            return true;
        }
        return !equalsTrim(existing.getName(), dto.getName())
                || !equalsTrim(existing.getAddress(), dto.getAddress())
                || !equalsTrim(existing.getTelephone(), dto.getTelephone())
                || !equalsTrim(existing.getFax(), dto.getFax())
                || !equalsTrim(existing.getEmail(), dto.getEmail())
                || !equalsTrim(existing.getWebSite(), dto.getWebSite());
    }

    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private boolean equalsTrim(String left, String right) {
        String a = left == null ? "" : left.trim();
        String b = right == null ? "" : right.trim();
        return a.equalsIgnoreCase(b);
    }

    @Override
    public void deletePublishers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("No records selected");
        }
        publisherRepository.deleteAllById(ids);
    }

    private void validatePublisher(PublisherEntity publisher) {
        if (publisher == null) {
            throw new ValidationException("PublisherEntity cannot be null");
        }
        if (publisher.getName() == null || publisher.getName().isBlank()) {
            throw new ValidationException("Name is required");
        }
        if (publisher.getEmail() != null && !publisher.getEmail().isBlank()
                && !publisher.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException(messageSource.getMessage("Pattern.email", null, LocaleContextHolder.getLocale()));
        }
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

    private boolean isPublisherExactMatch(PublisherEntity publisher, String normalizedQuery) {
        if (publisher == null || publisher.getName() == null) {
            return false;
        }
        return publisher.getName().trim().toLowerCase(Locale.ROOT).equals(normalizedQuery);
    }
}
