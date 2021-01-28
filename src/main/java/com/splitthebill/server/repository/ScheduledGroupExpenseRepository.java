package com.splitthebill.server.repository;

import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.user.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ScheduledGroupExpenseRepository extends CrudRepository<ScheduledGroupExpense, Long> {
    List<ScheduledGroupExpense> findAllByCreditor_Person(Person creditor);
    @Query("select s from ScheduledGroupExpense s where s.schedule.nextTrigger <= :creationDateTime")
    List<ScheduledGroupExpense> findAllWithNextTriggerDateBefore(
            @Param("creationDateTime") LocalDate creationDateTime);
}
