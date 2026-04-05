package com.example.bookportal.controller;

import com.example.bookportal.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Abstract base controller providing common response handling and logging.
 * <p>
 * Includes utility methods for standardized API responses, error handling, and
 * pagination.
 */
public abstract class BaseController {
    /**
     * Logger for controller classes.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Returns a successful API response with data.
     * 
     * @param data response payload
     * @param <T>  payload type
     * @return HTTP 200 response with payload
     */
    protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, data, null));
    }

    /**
     * Ensures page number is non-negative.
     * 
     * @param page requested page number
     * @return safe page number
     */
    protected int safePage(int page) {
        return Math.max(page, 0);
    }

    /**
     * Clamps a page size to the default controller range [5, 100].
     * 
     * @param size requested page size
     * @return bounded page size
     */
    protected int safePageSize(int size) {
        return Math.min(Math.max(size, 5), 100);
    }

    /**
     * Clamps a page size to a caller-provided range.
     * 
     * @param size requested page size
     * @param min  lower bound (inclusive)
     * @param max  upper bound (inclusive)
     * @return bounded page size
     */
    protected int boundedPageSize(int size, int min, int max) {
        return Math.min(Math.max(size, min), max);
    }

    /**
     * Builds a pageable with normalized page/size and dynamic sort.
     *
     * @param page        requested page index
     * @param size        requested page size
     * @param sort        requested sort field
     * @param direction   requested sort direction
     * @param minSize     minimum page size
     * @param maxSize     maximum page size
     * @param defaultSort fallback sort field when empty
     * @return pageable with normalized bounds and sort
     */
    protected Pageable pageable(int page,
            int size,
            String sort,
            String direction,
            int minSize,
            int maxSize,
            String defaultSort) {
        int safePage = safePage(page);
        int safeSize = boundedPageSize(size, minSize, maxSize);
        String safeSort = (sort == null || sort.isBlank()) ? defaultSort : sort;
        Sort.Direction safeDirection = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        return PageRequest.of(safePage, safeSize, Sort.by(safeDirection, safeSort));
    }

    /**
     * Adds common page metadata attributes expected by list views.
     *
     * @param model         view model
     * @param results       paged query results
     * @param requestedSize requested page size
     */
    protected void addPageMeta(Model model, Page<?> results, int requestedSize) {
        model.addAttribute("pageNumber", results.getNumber());
        model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("pageSize", safePageSize(requestedSize));
    }

    protected List<Long> uniquePositiveIds(List<Long> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Long> unique = new LinkedHashSet<>();
        for (Long id : rawIds) {
            if (id != null && id > 0) {
                unique.add(id);
            }
        }
        return new ArrayList<>(unique);
    }

    protected String showDeleteConfirmation(Model model,
            String messageKey,
            String confirmAction,
            String cancelUrl,
            Map<String, List<String>> hiddenParams) {
        model.addAttribute("confirmTitleKey", "delete");
        model.addAttribute("confirmMessageKey", messageKey);
        model.addAttribute("confirmAction", confirmAction);
        model.addAttribute("cancelUrl", cancelUrl);
        model.addAttribute("confirmHiddenParams", hiddenParams == null ? Map.of() : hiddenParams);
        return "delete-confirmation";
    }

    protected Map<String, List<String>> hiddenParamsForIds(String idParamName,
            List<Long> ids,
            Map<String, String> extraParams) {
        LinkedHashMap<String, List<String>> hidden = new LinkedHashMap<>();
        if (ids != null && !ids.isEmpty()) {
            List<String> idValues = ids.stream().map(String::valueOf).toList();
            hidden.put(idParamName, idValues);
        }
        if (extraParams != null && !extraParams.isEmpty()) {
            for (Map.Entry<String, String> entry : extraParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isBlank()) {
                    hidden.put(entry.getKey(), List.of(entry.getValue()));
                }
            }
        }
        return hidden;
    }
}
