package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.user.PersonGroup;
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

    private boolean isReviewed = false;

    @ManyToOne
    private PersonGroup debtor;

    @ManyToOne
    private GroupExpense expense;

    public PersonGroupExpense(double splitRatio, PersonGroup debtor, GroupExpense expense) {
        this.splitRatio = BigDecimal.valueOf(splitRatio);
        this.debtor = debtor;
        this.expense = expense;
    }
}
