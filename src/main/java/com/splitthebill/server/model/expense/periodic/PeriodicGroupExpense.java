package com.splitthebill.server.model.expense.periodic;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.Expense;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PeriodicGroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Group group;

    @ManyToOne
    private PeriodicExpenseSchedule schedule;

}