package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.expense.PersonGroupExpense;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PersonGroupExpenseReadDto {

    public Long debtorId;
    public String name;
    public int weight;

    public PersonGroupExpenseReadDto(PersonGroupExpense personGroupExpense) {
        this.debtorId = personGroupExpense.getDebtor().getId();
        this.name = personGroupExpense.getDebtor().getPerson().getName();
        this.weight = personGroupExpense.getWeight();
    }
}
