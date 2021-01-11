package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.user.Person;
import lombok.Data;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class Expense {

    private String title;

    private BigDecimal amount;

    private Date created;

    private String receiptPhoto;

    @ManyToOne
    private Person creditor;

    @ManyToOne
    private Currency currency;

}
