package com.splitthebill.server.dto.expense.scheduled.group;

import com.splitthebill.server.model.expense.scheduled.group.ScheduledPersonGroupExpense;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ScheduledExpenseParticipantReadDto {

    @Min(1)
    public Long debtorId;

    @Min(1)
    @Max(10)
    public int weight = 1;

    public String name;

    public ScheduledExpenseParticipantReadDto(ScheduledPersonGroupExpense expense) {
        this.debtorId = expense.getDebtor().getId();
        this.weight = expense.getWeight();
        this.name = expense.getDebtor().getPerson().getName();
    }
}
