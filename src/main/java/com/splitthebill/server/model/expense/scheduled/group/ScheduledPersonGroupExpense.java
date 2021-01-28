package com.splitthebill.server.model.expense.scheduled.group;

import com.splitthebill.server.model.user.PersonGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduledPersonGroupExpense {

    @Id
    @GeneratedValue
    private Long id;

    @Min(0)
    @Max(10)
    private int weight;

    @ManyToOne
    private PersonGroup debtor;

    @ManyToOne
    private ScheduledGroupExpense scheduledGroupExpense;

}
