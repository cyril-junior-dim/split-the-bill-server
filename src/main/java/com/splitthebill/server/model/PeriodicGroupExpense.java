package com.splitthebill.server.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PeriodicGroupExpense extends GroupExpense {

    @OneToOne
    private PeriodicExpenseSchedule schedule;

}