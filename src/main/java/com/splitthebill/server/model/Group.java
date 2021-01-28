package com.splitthebill.server.model;

import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        PersonGroup creditor = expense.getCreditor();
        creditor.addToBalance(currency, amount);
        BigDecimal totalWeight = BigDecimal.valueOf(expense.getTotalWeight());
        BigDecimal amountLeft = amount;
        List<PersonGroupExpense> participants = expense.getPersonGroupExpenses();
        for (PersonGroupExpense participant : participants) {
            BigDecimal splitRatio = BigDecimal.valueOf(participant.getWeight())
                    .divide(totalWeight, 4, RoundingMode.HALF_UP);
            BigDecimal toSubtract = amount.multiply(splitRatio).setScale(2, RoundingMode.HALF_UP);
            System.out.println("toSubtract " + toSubtract);
            participant.getDebtor().subtractFromBalance(currency, toSubtract);
            amountLeft = amountLeft.subtract(toSubtract);
            System.out.println("left " + amountLeft);
        }
        if (!amountLeft.equals(BigDecimal.ZERO))
            creditor.addToBalance(currency, amountLeft.negate());
    }

    public void addMember(PersonGroup member) {
        members.add(member);
    }

}
