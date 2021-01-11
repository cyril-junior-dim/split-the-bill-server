package com.splitthebill.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

}
