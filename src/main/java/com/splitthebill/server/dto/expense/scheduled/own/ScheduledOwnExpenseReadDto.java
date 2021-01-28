package com.splitthebill.server.dto.expense.scheduled.own;

import com.splitthebill.server.dto.expense.scheduled.ScheduleReadDto;
import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class ScheduledOwnExpenseReadDto {

    public Long id;
    public String title;
    public BigDecimal amount;
    public String currency;
    public ScheduleReadDto schedule;

    public ScheduledOwnExpenseReadDto(ScheduledOwnExpense ownExpense) {
        this.id = ownExpense.getId();
        this.title = ownExpense.getTitle();
        this.amount = ownExpense.getAmount();
        this.currency = ownExpense.getCurrency().getAbbreviation();
        this.schedule = new ScheduleReadDto(ownExpense.getSchedule());
    }

}
