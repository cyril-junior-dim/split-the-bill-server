package com.splitthebill.server.model.user;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.expense.periodic.PeriodicPersonGroupExpense;
import com.splitthebill.server.model.user.Friendship;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.model.user.UserAccount;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal overallBalance;

    @OneToOne
    private UserAccount userAccount;

    @OneToMany(mappedBy = "person1")
    private List<Friendship> friendships;

    @OneToMany(mappedBy = "person")
    private List<PersonGroup> personGroups;

    @OneToMany(mappedBy = "debtor")
    private List<PersonGroupExpense> personGroupExpenses;

    @OneToMany(mappedBy = "debtor")
    private List<PeriodicPersonGroupExpense> periodicPersonGroupExpenses;

    @ManyToOne
    private Currency preferredCurrency;

}
