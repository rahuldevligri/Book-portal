package com.example.bookportal.service;

import com.example.bookportal.dto.UserTypeDTO;
import com.example.bookportal.entity.UserTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service interface for managing user types.
 */
public interface UserTypeService {
    /**
     * Retrieves all user types.
     *
     * @return list of user types
     */
    List<UserTypeEntity> findAll();

    /**
     * Retrieves paginated user types.
     *
     * @param pageable pagination and sort
     * @return paged user types
     */
    Page<UserTypeEntity> findPage(Pageable pageable);

    /**
     * Finds a user type by ID.
     *
     * @param id user type ID
     * @return user type entity
     */
    UserTypeEntity findById(Long id);

    /**
     * Creates a new user type.
     *
     * @param type user type entity
     * @return created user type
     */
    UserTypeEntity create(UserTypeEntity type);

    /**
     * Updates an existing user type.
     *
     * @param id   user type ID
     * @param type updated user type entity
     * @return updated user type
     */
    UserTypeEntity update(Long id, UserTypeEntity type);

    /**
     * Converts user type DTO to entity.
     *
     * @param dto source DTO
     * @return mapped entity
     */
    UserTypeEntity fromDto(UserTypeDTO dto);

    /**
     * Converts user type entity to DTO.
     *
     * @param entity source entity
     * @return mapped DTO
     */
    UserTypeDTO toDto(UserTypeEntity entity);

    /**
     * Checks if the edit payload changes the current user type.
     *
     * @param existing persisted user type
     * @param dto      incoming DTO
     * @return true when values differ, false otherwise
     */
    boolean hasChanges(UserTypeEntity existing, UserTypeDTO dto);

    /**
     * Deletes user types by their IDs.
     *
     * @param ids list of user type IDs
     */
    void delete(List<Long> ids);
}
