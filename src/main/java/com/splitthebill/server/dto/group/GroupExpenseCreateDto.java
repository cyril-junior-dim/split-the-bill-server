package com.splitthebill.server.dto.group;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class GroupExpenseCreateDto {

    @Min(1)
    public Long groupId;

    @NotBlank
    public String title;

    @Min(1)
    public Long creditorId;

    @NotEmpty
    public List<@Valid ExpenseParticipantCreateDto> debtors;

    @DecimalMin("0.01")
    public double amount;

    @NotEmpty
    public String currencyAbbreviation;

}
