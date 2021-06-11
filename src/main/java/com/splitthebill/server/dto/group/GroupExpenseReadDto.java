package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.expense.GroupExpense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupExpenseReadDto {

    Long expenseId;
    Long creditorMemberId;
    String title;
    List<Long> debtorMemberIds;
    BigDecimal amount;
    String currency;
    LocalDateTime created;

    public GroupExpenseReadDto(GroupExpense expense) {
        this.expenseId = expense.getId();
        this.creditorMemberId = expense.getCreditor().getId();
        this.title = expense.getTitle();
        this.debtorMemberIds = expense.getPersonGroupExpenses().stream()
                .map(participant -> participant.getDebtor().getId())
                .collect(Collectors.toList());
        this.amount = expense.getAmount();
        this.currency = expense.getCurrency().getAbbreviation();
        this.created = expense.getCreated();
    }
}
