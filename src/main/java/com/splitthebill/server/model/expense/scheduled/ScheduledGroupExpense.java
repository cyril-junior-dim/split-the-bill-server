package com.splitthebill.server.model.expense.scheduled;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.Expense;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ScheduledGroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Group group;

    @ManyToOne
    private Schedule schedule;

    @OneToMany(mappedBy = "scheduledGroupExpense")
    List<ScheduledPersonGroupExpense> scheduledPersonGroupExpenses;

}