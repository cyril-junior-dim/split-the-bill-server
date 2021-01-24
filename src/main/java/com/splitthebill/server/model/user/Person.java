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
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Person extends RepresentationModel<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal overallBalance = BigDecimal.ZERO;

    @JsonIgnore
    @OneToOne(cascade=CascadeType.ALL)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "person1")
    private List<Friendship> friendships = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<PersonGroup> personGroups = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<OwnExpense> ownExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "creditor")
    private List<GroupExpense> groupExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "debtor")
    private List<PersonGroupExpense> personGroupExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "debtor")
    private List<ScheduledPersonGroupExpense> scheduledPersonGroupExpenses = new ArrayList<>();

    @ManyToOne
    private Currency preferredCurrency;

    public Person(UserAccount userAccount, String name){
        this.userAccount = userAccount;
        this.name = name;
    }

    public boolean isMemberOfGroup(Long groupId) {
        return getPersonGroups().stream().anyMatch(x -> x.getGroup().getId().equals(groupId));
    }

}
