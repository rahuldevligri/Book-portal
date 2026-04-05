package com.example.bookportal.service;

import com.example.bookportal.dto.SearchRequestDTO;
import com.example.bookportal.dto.SearchResultDTO;

/**
 * Service interface for searching books and related entities.
 */
public interface SearchService {
    /**
     * Searches for books and related entities using the provided search request.
     *
     * @param request search parameters
     * @return search result
     */
    SearchResultDTO search(SearchRequestDTO request);

}
