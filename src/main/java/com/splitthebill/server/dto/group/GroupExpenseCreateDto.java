package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.expense.GroupExpense;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class GroupExpenseCreateDto {

    @NotBlank
    public String title;

    @Min(1)
    public Long creditorId;

    @NotEmpty
    public List<@Valid ExpenseParticipantCreateDto> debtors;

    @DecimalMin("0.01")
    public BigDecimal amount;

    @NotEmpty
    public String currencyAbbreviation;

    public GroupExpenseCreateDto(GroupExpense settleUpExpense) {
        this.title = settleUpExpense.getTitle();
        this.creditorId = settleUpExpense.getCreditor().getId();
        this.debtors = settleUpExpense.getPersonGroupExpenses()
                .stream()
                .map(personGroupExpense -> new ExpenseParticipantCreateDto(personGroupExpense.getDebtor().getId(),
                        personGroupExpense.getWeight()))
                .collect(Collectors.toList());
        this.amount = settleUpExpense.getAmount();
        this.currencyAbbreviation = settleUpExpense.getCurrency().getAbbreviation();
    }

}
