package com.splitthebill.server.dto.expense.scheduled;

import com.splitthebill.server.dto.expense.scheduled.group.ScheduledGroupExpenseReadDto;
import com.splitthebill.server.dto.expense.scheduled.own.ScheduledOwnExpenseReadDto;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ScheduledExpenseReadDto {

    public List<ScheduledGroupExpenseReadDto> group = new ArrayList<>();
    public List<ScheduledOwnExpenseReadDto> own = new ArrayList<>();

    public ScheduledExpenseReadDto(List<ScheduledGroupExpense> groupExpenses, List<ScheduledOwnExpense> ownExpenses) {
        this.group = groupExpenses
                .stream()
                .map(ScheduledGroupExpenseReadDto::new)
                .collect(Collectors.toList());
        this.own = ownExpenses
                .stream()
                .map(ScheduledOwnExpenseReadDto::new)
                .collect(Collectors.toList());
    }
}
