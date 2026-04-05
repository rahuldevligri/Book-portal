
package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "book_category")
public class CategoryEntity extends BaseEntity {
    @Column(name = "CATEGORY")
    private String category;
}

