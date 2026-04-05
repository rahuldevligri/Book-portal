package com.example.bookportal.controller;

import com.example.bookportal.dto.SearchRequestDTO;
import com.example.bookportal.dto.SearchResultDTO;
import com.example.bookportal.service.SearchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for search operations.
 * <p>
 * Handles API requests for searching books and related entities.
 */
@RestController
@RequestMapping("/api")
public class SearchController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    private SearchService searchService;

    /**
     * Searches for books and related entities using the provided search request.
     * 
     * @param request search request DTO
     * @return search result DTO wrapped in ResponseEntity
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResultDTO> search(@Valid @RequestBody SearchRequestDTO request) {
        logger.info("Search requested: query={}, type={}, page={}, size={}, sort={}, direction={}",
                request.getQuery(), request.getType(), request.getPage(), request.getSize(), request.getSort(),
                request.getDirection());
        SearchResultDTO result = searchService.search(request);
        logger.info("Search completed: totalElements={}, totalPages={}, page={}",
                result.getTotalElements(), result.getTotalPages(), result.getPage());
        return ResponseEntity.ok(result);
    }
}
