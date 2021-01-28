package com.splitthebill.server.model.expense.scheduled.own;

import com.splitthebill.server.model.expense.Expense;
import com.splitthebill.server.model.expense.scheduled.Schedule;
import com.splitthebill.server.model.user.Person;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduledOwnExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Schedule schedule;

    @ManyToOne
    private Person person;

}
