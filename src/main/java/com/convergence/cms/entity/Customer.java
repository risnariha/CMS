package com.convergence.cms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dob;

    @Column(unique = true)
    private String nic;

    @ElementCollection
    private List<String> mobileNumbers = new ArrayList<>();

    @ManyToMany
    private List<Customer> familyMembers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();
}
