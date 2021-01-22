package com.splitthebill.server.dto.group;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

public class ExpenseParticipantCreateDto {

    @Min(1)
    public Long debtorId;

    @DecimalMax("0.99")
    @DecimalMin("0.01")
    public double splitRatio;

}
