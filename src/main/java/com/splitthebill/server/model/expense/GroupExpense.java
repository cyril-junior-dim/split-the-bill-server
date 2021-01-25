package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Group group;

    @ManyToOne
    private PersonGroup creditor;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.PERSIST)
    private List<PersonGroupExpense> personGroupExpenses;

    public int getTotalWeight() {
        return personGroupExpenses.stream()
                .map(PersonGroupExpense::getWeight)
                .reduce(0, Integer::sum);
    }

}
