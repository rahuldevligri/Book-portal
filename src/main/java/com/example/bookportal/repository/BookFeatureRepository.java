package com.example.bookportal.repository;

import com.example.bookportal.entity.BookFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository for BookFeatureEntity.
 */
public interface BookFeatureRepository extends JpaRepository<BookFeatureEntity, Long> {
}


