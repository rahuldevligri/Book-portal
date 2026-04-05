package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_types")
public class UserTypeEntity extends BaseEntity {
    @Column(name = "TYPE")
    private String type;
}
