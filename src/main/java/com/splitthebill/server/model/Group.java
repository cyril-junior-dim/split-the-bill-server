package com.splitthebill.server.model;

import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Column(name = "`name`")
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
        int totalWeight = expense.getTotalWeight();
        for (PersonGroupExpense personGroupExpense : expense.getPersonGroupExpenses()) {
            BigDecimal splitRatio = BigDecimal.valueOf(personGroupExpense.getWeight())
                    .divide(BigDecimal.valueOf(totalWeight), 2, RoundingMode.HALF_UP);
            BigDecimal toSubtract = amount.multiply(splitRatio);
            personGroupExpense.getDebtor().subtractFromBalance(currency, toSubtract);
        }
    }

    public void addMember(PersonGroup member) {
        members.add(member);
    }

}
