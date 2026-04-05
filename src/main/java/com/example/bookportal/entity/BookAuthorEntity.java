package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book_author")
public class BookAuthorEntity extends BaseEntity {
    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "AUTHOR_ID")
    private Long authorId;
}
