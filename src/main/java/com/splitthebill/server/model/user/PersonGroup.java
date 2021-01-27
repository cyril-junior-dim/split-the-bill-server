package com.splitthebill.server.model.user;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.Expense;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PersonGroup {

    @Id
    @GeneratedValue
    private Long id;

    @CreationTimestamp
    private Date joined;

    @ManyToOne
    private Person person;

    @ManyToOne
    private Group group;

    @ElementCollection
    @CollectionTable(name = "person_group_balance")
    @MapKeyJoinColumn(name = "currency_id")
    @Column(name = "balance")
    private Map<Currency, BigDecimal> balances = new HashMap<>();

    public PersonGroup(Person person, Group group) {
        this.person = person;
        this.group = group;
        this.balances.put(person.getPreferredCurrency(), BigDecimal.ZERO);
    }

    public void addToBalance(Currency currency, BigDecimal amount) {
        if (balances.containsKey(currency)) {
            BigDecimal newBalance = balances.computeIfPresent(currency, (c, currBalance) -> currBalance.add(amount));
            if (newBalance.equals(BigDecimal.ZERO) && !currency.equals(person.getPreferredCurrency())) {
                balances.remove(currency);
            }
        } else balances.put(currency, amount);
    }

    public void subtractFromBalance(Currency currency, BigDecimal amount) {
        addToBalance(currency, amount.negate());
    }

    public List<GroupExpense> getSettleUpExpenses() {
        return balances.keySet().stream().map(this::getSettleUpExpenses).reduce(
                new ArrayList<>(), (allExpenses, currencyExpenses) -> {
                    allExpenses.addAll(currencyExpenses);
                    return allExpenses;
                }
        );
    }

    public List<GroupExpense> getSettleUpExpenses(Currency currency) {
        BigDecimal balance = balances.get(currency).negate();
        if(balance.compareTo(BigDecimal.ZERO) <= 0)
            return List.of();

        Stack<PersonGroup> positiveBalanceMembers = this.group.getMembers()
                .stream()
                .filter(personGroup -> personGroup.balances.getOrDefault(currency, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0)
                .filter(personGroup -> !personGroup.equals(this))
                .sorted(Comparator.comparing(p -> p.balances.get(currency)))
                .collect(Collectors.toCollection(Stack::new));
        List<GroupExpense> expenses = new ArrayList<>();

        System.out.println(currency);
        positiveBalanceMembers.forEach(p -> System.out.println(p.person.getName()));

        while(balance.compareTo(BigDecimal.ZERO) > 0) {
            PersonGroup receiver = positiveBalanceMembers.pop();
            BigDecimal amountDue = balance.min(receiver.getBalances().get(currency));
            GroupExpense groupExpense = new GroupExpense();
            groupExpense.setGroup(this.group);
            groupExpense.setCreditor(this);
            groupExpense.setTitle("Settle up");
            groupExpense.setCurrency(currency);
            groupExpense.setAmount(amountDue);

            PersonGroupExpense issuerExpense = new PersonGroupExpense();
            issuerExpense.setDebtor(receiver);
            issuerExpense.setWeight(1);
            issuerExpense.setExpense(groupExpense);

            groupExpense.setPersonGroupExpenses(List.of(issuerExpense));

            expenses.add(groupExpense);
            balance = balance.subtract(amountDue);
        }
        return expenses;
    }

}
