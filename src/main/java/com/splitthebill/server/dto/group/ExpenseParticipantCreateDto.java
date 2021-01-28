package com.splitthebill.server.dto.group;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
public class ExpenseParticipantCreateDto {

    @Min(1)
    public Long debtorId;

    @Min(1)
    @Max(10)
    public int weight = 1;

}
