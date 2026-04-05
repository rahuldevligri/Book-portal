package com.example.bookportal.repository;

import com.example.bookportal.entity.BookPublisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookPublisherRepository extends JpaRepository<BookPublisherEntity, Long> {
    /**
     * Finds mappings for multiple book IDs ordered by mapping ID.
     *
     * @param bookIds list of book IDs
     * @return list of BookPublisherEntity mappings
     */
    List<BookPublisherEntity> findByBookIdInOrderByIdAsc(List<Long> bookIds);
}


