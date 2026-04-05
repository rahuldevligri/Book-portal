package com.example.bookportal.service.impl;

import com.example.bookportal.entity.SecretQuestionEntity;
import com.example.bookportal.repository.SecretQuestionRepository;
import com.example.bookportal.service.SecretQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing secret questions.
 */
@Service
public class SecretQuestionServiceImpl implements SecretQuestionService {

    @Autowired
    private SecretQuestionRepository secretQuestionRepository;
    @Autowired
    private MessageSource messageSource;

    /**
     * Retrieves all secret questions.
     *
     * @return list of secret question entities
     */
    @Override
    public List<SecretQuestionEntity> findAll() {
        return secretQuestionRepository.findAll();
    }

    /**
     * Finds a secret question by its ID.
     *
     * @param id the secret question ID
     * @return optional secret question entity
     */
    @Override
    public Optional<SecretQuestionEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return secretQuestionRepository.findById(id);
    }

    @Override
    public Map<Long, String> findAllLocalizedQuestionMap() {
        return findAll().stream()
                .collect(Collectors.toMap(
                        SecretQuestionEntity::getId,
                        q -> messageSource.getMessage(q.getQuestion(), null, q.getQuestion(),
                                LocaleContextHolder.getLocale())));
    }
}
