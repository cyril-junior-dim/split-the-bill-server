package com.splitthebill.server.dto.group;

import javax.validation.constraints.Min;

public class ExpenseParticipantCreateDto {

    @Min(1)
    public Long debtorId;

    public double splitRatio;

}
