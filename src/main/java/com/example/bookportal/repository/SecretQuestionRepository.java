package com.example.bookportal.repository;

import com.example.bookportal.entity.SecretQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for SecretQuestionEntity.
 */
public interface SecretQuestionRepository extends JpaRepository<SecretQuestionEntity, Long> {
}


