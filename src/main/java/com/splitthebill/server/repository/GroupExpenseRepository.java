package com.splitthebill.server.repository;

import com.splitthebill.server.model.expense.GroupExpense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupExpenseRepository extends CrudRepository<GroupExpense, Long> {

    List<GroupExpense> getAllByCreditor_Id(Long creditorId);

}
