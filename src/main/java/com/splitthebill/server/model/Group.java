package com.splitthebill.server.model;

import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
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

    @OneToMany(mappedBy = "group", cascade = CascadeType.PERSIST)
    private List<PersonGroup> members = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.PERSIST)
    private List<GroupExpense> expenses = new ArrayList<>();

    public void addExpense(GroupExpense expense) {
        expenses.add(expense);
        BigDecimal amount = expense.getAmount();
        Currency currency = expense.getCurrency();
        expense.getCreditor().addToBalance(currency, amount);
        for (PersonGroupExpense personGroupExpense : expense.getPersonGroupExpenses()) {
            BigDecimal toSubtract = amount.multiply(personGroupExpense.getSplitRatio());
            personGroupExpense.getDebtor().subtractFromBalance(currency, toSubtract);
        }
    }
}
