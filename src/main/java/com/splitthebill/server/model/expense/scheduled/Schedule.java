package com.splitthebill.server.model.expense.scheduled;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    private Date nextTrigger;

}