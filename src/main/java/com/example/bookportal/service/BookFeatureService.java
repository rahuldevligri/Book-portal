package com.example.bookportal.service;

import com.example.bookportal.dto.BookFeatureDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing book features
 */
public interface BookFeatureService {

    /**
     * Retrieves features for multiple books organized by book ID.
     *
     * @param bookIds list of book IDs
     * @return map of bookId to list of features
     */
    Map<Long, List<BookFeatureDTO>> getBooksFeatures(List<Long> bookIds);
}
