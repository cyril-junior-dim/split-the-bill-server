package com.splitthebill.server.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.expense.Expense;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.OwnExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.expense.scheduled.ScheduledPersonGroupExpense;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Person extends RepresentationModel<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal overallBalance;

    @JsonIgnore
    @OneToOne
    private UserAccount userAccount;

    @OneToMany(mappedBy = "person1")
    private List<Friendship> friendships;

    @OneToMany(mappedBy = "person")
    private List<PersonGroup> personGroups;

    @OneToMany(mappedBy = "owner")
    private List<OwnExpense> ownExpenses;

    @OneToMany(mappedBy = "creditor")
    private List<GroupExpense> groupExpenses;

    @OneToMany(mappedBy = "debtor")
    private List<PersonGroupExpense> personGroupExpenses;

    @OneToMany(mappedBy = "debtor")
    private List<ScheduledPersonGroupExpense> scheduledPersonGroupExpenses;

    @ManyToOne
    private Currency preferredCurrency;

    protected Person(){

    }

    public Person(UserAccount userAccount, String name){
        this.userAccount = userAccount;
        this.name = name;
        this.overallBalance = BigDecimal.ZERO;
        this.friendships = List.of();
        this.personGroups = List.of();
        this.ownExpenses = List.of();
        this.groupExpenses = List.of();
        this.personGroupExpenses = List.of();
        this.scheduledPersonGroupExpenses = List.of();
    }
}
