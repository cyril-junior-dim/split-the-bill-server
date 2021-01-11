package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class PeriodicExpenseSchedule {

    @Id
    @GeneratedValue
    private Long id;

    private int amount;

    private FrequencyUnit frequencyUnit;

    private Date nextTrigger;

}