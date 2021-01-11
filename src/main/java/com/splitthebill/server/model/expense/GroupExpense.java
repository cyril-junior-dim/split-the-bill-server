package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class GroupExpense extends Expense {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Group group;

    @OneToMany(mappedBy = "expense")
    private List<PersonGroupExpense> personGroupExpenses;
}
