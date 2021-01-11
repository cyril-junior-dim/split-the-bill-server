package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double overallBalance;

    @OneToOne
    private UserAccount userAccount;

    @OneToMany
    private List<Friendship> friendships;

    @ManyToOne
    private Currency preferredCurrency;

}
