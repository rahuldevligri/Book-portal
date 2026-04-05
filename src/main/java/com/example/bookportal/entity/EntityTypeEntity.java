package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "entity_type")
@EqualsAndHashCode(callSuper = true)
public class EntityTypeEntity extends BaseEntity {
    @Column(name = "NAME")
    private String name;
}
