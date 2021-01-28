package com.splitthebill.server.dto.expense.scheduled;

import com.splitthebill.server.model.expense.scheduled.Schedule;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class ScheduleReadDto {

    public int amount;
    public String frequencyUnit;
    public String nextTrigger;

    public ScheduleReadDto(Schedule schedule) {
        this.amount = schedule.getAmount();
        this.frequencyUnit = schedule.getFrequencyUnit().name();
        this.nextTrigger = schedule.getNextTrigger().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

}
