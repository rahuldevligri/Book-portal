package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "secret_question")
public class SecretQuestionEntity extends BaseEntity {
    @Column(name = "QUESTION")
    private String question;
}
