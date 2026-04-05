package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book_feature_values")
public class BookFeatureValuesEntity extends BaseEntity {
    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "BOOK_FEATURE_ID")
    private Long bookFeatureId;

    @Column(name = "VALUE")
    private String value;
}
