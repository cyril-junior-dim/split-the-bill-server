package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.*;
import org.hibernate.validator.cfg.context.Cascadable;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
public class GroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Group group;

    @ManyToOne
    private PersonGroup creditor;

    @OneToMany(mappedBy = "expense", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<PersonGroupExpense> personGroupExpenses;

    public int getTotalWeight() {
        return personGroupExpenses.stream()
                .map(PersonGroupExpense::getWeight)
                .reduce(0, Integer::sum);
    }

}
