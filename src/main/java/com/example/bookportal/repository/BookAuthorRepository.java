package com.example.bookportal.repository;

import com.example.bookportal.entity.BookAuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthorEntity, Long> {
    /**
     * Finds mappings for multiple book IDs ordered by mapping ID.
     *
     * @param bookIds list of book IDs
     * @return list of BookAuthorEntity mappings
     */
    List<BookAuthorEntity> findByBookIdInOrderByIdAsc(List<Long> bookIds);
}


