package com.convergence.cms.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String line1;
    private String line2;

    @ManyToOne
    private City city;

    @ManyToOne
    private Country country;
}