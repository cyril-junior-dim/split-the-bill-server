package com.splitthebill.server.dto.group;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class GroupExpenseCreateDto {

    @Min(1)
    public Long groupId;

    @Min(1)
    public Long creditorId;

    @NotEmpty
    public List<@Valid ExpenseParticipantCreateDto> debtors;

    public double amount;

}
