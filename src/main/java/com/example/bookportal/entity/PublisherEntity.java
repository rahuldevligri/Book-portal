package com.example.bookportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "publisher_distributor")
@EqualsAndHashCode(callSuper = true)
public class PublisherEntity extends BaseEntity {
    @Column(name = "NAME")
    private String name;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "TELEPHONE")
    private String telephone;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "WEB_SITE")
    private String webSite;

    @Column(name = "ENTITY_TYPE_ID")
    private Long entityTypeId;
}
