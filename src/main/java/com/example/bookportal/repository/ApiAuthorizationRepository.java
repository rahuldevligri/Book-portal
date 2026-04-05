package com.example.bookportal.repository;

import com.example.bookportal.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

import java.util.Set;

public interface ApiAuthorizationRepository extends Repository<UserEntity, Long> {

    /**
     * Finds API URLs accessible by a user based on their group memberships.
     *
     * @param userId the user ID
     * @return set of API URLs
     */
    @Query(value = """
            SELECT DISTINCT api.api_url
            FROM app_groups_users gu
            JOIN app_groups_apis ga ON ga.groups_fk = gu.groups_fk
            JOIN app_apis api ON api.apis_pk = ga.apis_fk
            WHERE gu.users_fk = :userId
            """, nativeQuery = true)
    Set<String> findApiUrlsByUserId(@Param("userId") Long userId);
}


