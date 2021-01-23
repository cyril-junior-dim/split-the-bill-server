package com.splitthebill.server.model;

import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String photoPath;

    //TODO? boolean isSettledUp

    @OneToMany(mappedBy = "group")
    private List<PersonGroup> members;

    @OneToMany(mappedBy = "group")
    private List<GroupExpense> expenses;

    public void addExpense(GroupExpense expense) {
        expenses.add(expense);
        BigDecimal amount = expense.getAmount();
        expense.getCreditor().addToBalance(amount);
        for (PersonGroupExpense personGroupExpense : expense.getPersonGroupExpenses()) {
            BigDecimal toSubtract = amount.multiply(personGroupExpense.getSplitRatio());
            personGroupExpense.getDebtor().subtractFromBalance(toSubtract);
        }
    }
}
