package com.splitthebill.server.model.expense.scheduled;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;

    private int amount;

    private FrequencyUnit frequencyUnit;

    private LocalDate nextTrigger;

    public void tickTrigger() {
        switch(frequencyUnit) {
            case DAY:
                nextTrigger = nextTrigger.plusDays(amount);
            case WEEK:
                nextTrigger = nextTrigger.plusWeeks(amount);
            case MONTH:
                nextTrigger = nextTrigger.plusMonths(amount);
            case YEAR:
                nextTrigger = nextTrigger.plusYears(amount);
        }
    }
}