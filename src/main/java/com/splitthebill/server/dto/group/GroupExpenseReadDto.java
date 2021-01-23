package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.expense.GroupExpense;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupExpenseReadDto {

    Long expenseId;
    Long creditorMemberId;
    List<Long> creditorMemberIds;
    BigDecimal amount;

    public GroupExpenseReadDto(GroupExpense expense) {
        this.expenseId = expense.getId();
        this.creditorMemberId = expense.getCreditor().getId();
        this.creditorMemberIds = expense.getPersonGroupExpenses().stream()
                .map(participant -> participant.getDebtor().getId())
                .collect(Collectors.toList());
        this.amount = expense.getAmount();
    }
}
