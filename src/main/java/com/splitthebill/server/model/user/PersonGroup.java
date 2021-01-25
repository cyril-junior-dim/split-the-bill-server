package com.splitthebill.server.model.user;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
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
                System.out.println("Currency not preferred");
                balances.remove(currency);
            }
        } else balances.put(currency, amount);
    }

    public void subtractFromBalance(Currency currency, BigDecimal amount) {
        addToBalance(currency, amount.negate());
    }

}
