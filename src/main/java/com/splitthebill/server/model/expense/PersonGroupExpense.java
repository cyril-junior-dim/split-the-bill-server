package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
public class PersonGroupExpense {

    @Id
    @GeneratedValue
    private Long id;

    @Min(0)
    @Max(10)
    private int weight;

    private boolean isReviewed = false;

    @ManyToOne
    private PersonGroup debtor;

    @ManyToOne
    private GroupExpense expense;

    public PersonGroupExpense(int weight, PersonGroup debtor, GroupExpense expense) {
        this.weight = weight;
        this.debtor = debtor;
        this.expense = expense;
    }
}
