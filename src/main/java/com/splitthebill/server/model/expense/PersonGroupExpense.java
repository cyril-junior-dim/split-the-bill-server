package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.user.Person;
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
    private Person debtor;

    @ManyToOne
    private GroupExpense expense;

}
