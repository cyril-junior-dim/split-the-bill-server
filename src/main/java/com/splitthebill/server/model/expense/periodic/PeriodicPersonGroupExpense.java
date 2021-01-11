package com.splitthebill.server.model.expense.periodic;

import com.splitthebill.server.model.user.Person;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Data
@Entity
public class PeriodicPersonGroupExpense {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal splitRatio;

    @ManyToOne
    private Person debtor;

    @ManyToOne
    private PeriodicGroupExpense periodicGroupExpense;

}
