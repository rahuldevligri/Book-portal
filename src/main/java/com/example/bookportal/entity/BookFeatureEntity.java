package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book_feature")
public class BookFeatureEntity extends BaseEntity {
    @Column(name = "FEATURE_NAME")
    private String featureName;
}
