package com.example.bookportal.service;

import com.example.bookportal.entity.SecretQuestionEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SecretQuestionService {
    /**
     * Retrieves all secret questions.
     *
     * @return list of secret question entities
     */
    List<SecretQuestionEntity> findAll();

    /**
     * Finds a secret question by its ID.
     *
     * @param id the secret question ID
     * @return optional secret question entity
     */
    Optional<SecretQuestionEntity> findById(Long id);

    /**
     * Retrieves all secret questions as a localized map for display.
     *
     * @return map of question id to localized text
     */
    Map<Long, String> findAllLocalizedQuestionMap();
}
