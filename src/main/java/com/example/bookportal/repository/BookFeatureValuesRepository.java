package com.example.bookportal.repository;

import com.example.bookportal.entity.BookFeatureValuesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookFeatureValuesRepository extends JpaRepository<BookFeatureValuesEntity, Long> {
    /**
     * Finds all feature values for multiple books.
     *
     * @param bookIds list of book IDs
     * @return list of BookFeatureValuesEntity values
     */
    @Query("SELECT bfv FROM BookFeatureValuesEntity bfv WHERE bfv.bookId IN :bookIds")
    List<BookFeatureValuesEntity> findByBookIdIn(@Param("bookIds") List<Long> bookIds);
}


