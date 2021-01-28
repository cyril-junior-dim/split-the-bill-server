package com.splitthebill.server.repository;

import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScheduledGroupExpenseRepository extends CrudRepository<ScheduledGroupExpense, Long> {
    List<ScheduledGroupExpense> findAllByCreditor_Person(Person creditor);
}
