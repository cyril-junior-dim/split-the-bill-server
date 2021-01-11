package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
public class PersonGroupExpense {

    @Id
    @GeneratedValue
    private Long id;

    private double splitRatio;

    private boolean isReviewed;

    @ManyToOne
    Person debtor;

    @ManyToOne
    GroupExpense expense;

}
