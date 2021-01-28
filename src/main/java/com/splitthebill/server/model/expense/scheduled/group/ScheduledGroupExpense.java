package com.splitthebill.server.model.expense.scheduled.group;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.Expense;
import com.splitthebill.server.model.expense.scheduled.Schedule;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledPersonGroupExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduledGroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Group group;

    @ManyToOne
    private PersonGroup creditor;

    @ManyToOne(cascade = CascadeType.ALL)
    private Schedule schedule;

    @OneToMany(mappedBy = "scheduledGroupExpense", cascade = CascadeType.ALL)
    List<ScheduledPersonGroupExpense> scheduledPersonGroupExpenses;

}