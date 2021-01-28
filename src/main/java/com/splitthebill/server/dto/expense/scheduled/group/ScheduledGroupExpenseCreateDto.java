package com.splitthebill.server.dto.expense.scheduled.group;

import com.splitthebill.server.dto.expense.scheduled.ScheduleReadDto;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
public class ScheduledGroupExpenseCreateDto {

    public Long creditorId;
    public String title;
    public List<ScheduledExpenseParticipantCreateDto> debtors;
    public BigDecimal amount;
    public String currency;
    public ScheduleReadDto schedule;

}
