package com.example.bookportal.repository;

import com.example.bookportal.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository for CategoryEntity.
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}


