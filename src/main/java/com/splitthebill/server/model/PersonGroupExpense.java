package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class PersonGroupExpense {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal splitRatio;

    private boolean isReviewed;

    @ManyToOne
    Person debtor;

    @ManyToOne
    GroupExpense expense;

}
