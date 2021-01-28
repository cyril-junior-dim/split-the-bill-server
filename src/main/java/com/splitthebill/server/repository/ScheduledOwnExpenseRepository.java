package com.splitthebill.server.repository;

import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
import com.splitthebill.server.model.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScheduledOwnExpenseRepository extends CrudRepository<ScheduledOwnExpense, Long> {
    List<ScheduledOwnExpense> findAllByPerson(Person person);
}
