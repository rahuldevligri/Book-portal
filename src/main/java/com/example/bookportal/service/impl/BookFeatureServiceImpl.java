package com.example.bookportal.service.impl;

import com.example.bookportal.dto.BookFeatureDTO;
import com.example.bookportal.entity.BookFeatureEntity;
import com.example.bookportal.entity.BookFeatureValuesEntity;
import com.example.bookportal.repository.BookFeatureRepository;
import com.example.bookportal.repository.BookFeatureValuesRepository;
import com.example.bookportal.service.BookFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of BookFeatureService
 */
@Service
public class BookFeatureServiceImpl implements BookFeatureService {
        private static final Logger logger = LoggerFactory.getLogger(BookFeatureServiceImpl.class);

        @Autowired
        private BookFeatureRepository bookFeatureRepository;
        @Autowired
        private BookFeatureValuesRepository bookFeatureValuesRepository;

        /**
         * Retrieves features for multiple books organized by book ID.
         *
         * @param bookIds list of book IDs
         * @return map of bookId to list of features
         */
        @Override
        public Map<Long, List<BookFeatureDTO>> getBooksFeatures(List<Long> bookIds) {
                logger.debug("Fetching features for {} books", bookIds.size());

                if (bookIds == null || bookIds.isEmpty()) {
                        return Collections.emptyMap();
                }

                List<BookFeatureValuesEntity> allFeatureValues = bookFeatureValuesRepository.findByBookIdIn(bookIds);

                if (allFeatureValues.isEmpty()) {
                        logger.debug("No features found for the provided book IDs");
                        return bookIds.stream().collect(Collectors.toMap(id -> id, id -> Collections.emptyList()));
                }

                List<Long> featureIds = allFeatureValues.stream()
                                .map(BookFeatureValuesEntity::getBookFeatureId)
                                .distinct()
                                .collect(Collectors.toList());

                Map<Long, String> featureNamesMap = bookFeatureRepository.findAllById(featureIds).stream()
                                .collect(Collectors.toMap(BookFeatureEntity::getId, BookFeatureEntity::getFeatureName));

                Map<Long, List<BookFeatureDTO>> result = allFeatureValues.stream()
                                .collect(Collectors.groupingBy(
                                                BookFeatureValuesEntity::getBookId,
                                                Collectors.mapping(
                                                                fv -> new BookFeatureDTO(
                                                                                fv.getBookFeatureId(),
                                                                                featureNamesMap.getOrDefault(
                                                                                                fv.getBookFeatureId(),
                                                                                                "Unknown"),
                                                                                fv.getValue()),
                                                                Collectors.toList())));

                for (Long bookId : bookIds) {
                        result.putIfAbsent(bookId, Collections.emptyList());
                }

                return result;
        }
}
