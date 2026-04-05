package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book_publisher")
public class BookPublisherEntity extends BaseEntity {
    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "PUBLISHER_ID")
    private Long publisherId;
}
