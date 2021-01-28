package com.splitthebill.server.dto.expense.scheduled.group;

import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor
public class ScheduledExpenseParticipantCreateDto {

    @Min(1)
    public Long debtorId;

    @Min(1)
    @Max(10)
    public int weight = 1;

}
