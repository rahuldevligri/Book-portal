package com.example.bookportal.repository;

import com.example.bookportal.entity.UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository for UserTypeEntity.
 */
public interface UserTypeRepository extends JpaRepository<UserTypeEntity, Long> {
}


