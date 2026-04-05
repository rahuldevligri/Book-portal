package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_users")
public class UserEntity extends BaseEntity {
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "USER_NAME", unique = true)
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SECRET_QUESTION_ID")
    private Long secretQuestionId;

    @Column(name = "SECRET_ANSWER")
    private String secretAnswer;

    @Column(name = "USER_TYPE_ID")
    private Long userTypeId;
}
