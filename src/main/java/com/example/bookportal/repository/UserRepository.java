package com.example.bookportal.repository;

import com.example.bookportal.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
        /**
         * Finds a user by their username (case-insensitive).
         *
         * @param userName the username to search for
         * @return an Optional containing the user if found
         */
        Optional<UserEntity> findByUserNameIgnoreCase(String userName);

        /**
         * Finds a user by their email address (case-insensitive).
         *
         * @param email the email address to search for
         * @return an Optional containing the user if found
         */
        Optional<UserEntity> findByEmailIgnoreCase(String email);

        /**
         * Finds users by group ID with pagination.
         *
         * @param groupId  the group ID
         * @param pageable pagination information
         * @return page of UserEntity objects
         */
        @Query(value = "SELECT DISTINCT u FROM UserEntity u WHERE EXISTS (" +
                        "SELECT 1 FROM GroupUserEntity gu WHERE gu.user.id = u.id AND gu.group.id = :groupId" +
                        ")", countQuery = "SELECT COUNT(DISTINCT u) FROM UserEntity u WHERE EXISTS (" +
                                        "SELECT 1 FROM GroupUserEntity gu WHERE gu.user.id = u.id AND gu.group.id = :groupId"
                                        +
                                        ")")
        Page<UserEntity> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);
}


