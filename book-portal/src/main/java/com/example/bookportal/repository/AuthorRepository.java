package com.example.bookportal.repository;

import com.example.bookportal.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Used for Author Search page
    List<Author> findByAuthorNameContainingIgnoreCase(String authorName);
}
