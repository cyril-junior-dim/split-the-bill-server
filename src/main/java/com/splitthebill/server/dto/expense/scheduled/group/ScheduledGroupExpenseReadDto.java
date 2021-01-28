package com.splitthebill.server.dto.expense.scheduled.group;

import com.splitthebill.server.dto.expense.scheduled.ScheduleReadDto;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ScheduledGroupExpenseReadDto {

    public Long id;
    public Long groupId;
    public String groupName;
    public String title;
    public List<ScheduledExpenseParticipantReadDto> debtors;
    public BigDecimal amount;
    public String currency;
    public ScheduleReadDto schedule;

    public ScheduledGroupExpenseReadDto(ScheduledGroupExpense expense) {
        this.id = expense.getId();
        this.groupId = expense.getGroup().getId();
        this.groupName = expense.getGroup().getName();
        this.title = expense.getTitle();
        this.debtors = expense.getScheduledPersonGroupExpenses()
                .stream()
                .map(ScheduledExpenseParticipantReadDto::new)
                .collect(Collectors.toList());
        this.amount = expense.getAmount();
        this.currency = expense.getCurrency().getAbbreviation();
        this.schedule = new ScheduleReadDto(expense.getSchedule());
    }

}
