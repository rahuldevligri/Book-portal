package com.example.bookportal.service.impl;

import com.example.bookportal.dto.UserTypeDTO;
import com.example.bookportal.entity.UserTypeEntity;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.repository.UserTypeRepository;
import com.example.bookportal.service.UserTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeServiceImpl implements UserTypeService {

    @Autowired
    private UserTypeRepository userTypeRepository;

    /**
     * Retrieves all user types.
     *
     * @return list of user types
     */
    @Override
    public List<UserTypeEntity> findAll() {
        return userTypeRepository.findAll();
    }

    /**
     * Retrieves paginated user types.
     *
     * @param pageable pagination and sort
     * @return paged user types
     */
    @Override
    public Page<UserTypeEntity> findPage(Pageable pageable) {
        return userTypeRepository.findAll(pageable);
    }

    /**
     * Finds a user type by ID.
     *
     * @param id user type ID
     * @return user type entity
     */
    @Override
    public UserTypeEntity findById(Long id) {
        return userTypeRepository.findById(id)
                .orElseThrow(() -> new ValidationException("UserEntity type not found"));
    }

    /**
     * Creates a new user type.
     *
     * @param type user type entity
     * @return created user type
     */
    @Override
    public UserTypeEntity create(UserTypeEntity type) {
        validate(type);
        return userTypeRepository.save(type);
    }

    /**
     * Updates an existing user type.
     *
     * @param id   user type ID
     * @param type updated user type entity
     * @return updated user type
     */
    @Override
    public UserTypeEntity update(Long id, UserTypeEntity type) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid id");
        }
        UserTypeEntity existing = findById(id);
        validate(type);
        existing.setType(type.getType());
        return userTypeRepository.save(existing);
    }

    @Override
    public UserTypeEntity fromDto(UserTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        UserTypeEntity entity = new UserTypeEntity();
        entity.setType(dto.getType());
        return entity;
    }

    @Override
    public UserTypeDTO toDto(UserTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        UserTypeDTO dto = new UserTypeDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        return dto;
    }

    @Override
    public boolean hasChanges(UserTypeEntity existing, UserTypeDTO dto) {
        if (existing == null || dto == null) {
            return true;
        }
        return !equalsTrim(existing.getType(), dto.getType());
    }

    /**
     * Deletes user types by their IDs.
     *
     * @param ids list of user type IDs
     */
    @Override
    public void delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("No user types selected");
        }
        userTypeRepository.deleteAllById(ids);
    }

    private void validate(UserTypeEntity type) {
        if (type == null || type.getType() == null || type.getType().isBlank()) {
            throw new ValidationException("Type is required");
        }
    }

    private boolean equalsTrim(String left, String right) {
        String a = left == null ? "" : left.trim();
        String b = right == null ? "" : right.trim();
        return a.equalsIgnoreCase(b);
    }
}
