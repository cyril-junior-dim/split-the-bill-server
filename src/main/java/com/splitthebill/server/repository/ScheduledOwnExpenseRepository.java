package com.splitthebill.server.repository;

import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
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
public interface ScheduledOwnExpenseRepository extends CrudRepository<ScheduledOwnExpense, Long> {
    List<ScheduledOwnExpense> findAllByPerson(Person person);
    @Query("select s from ScheduledOwnExpense s where s.schedule.nextTrigger <= :creationDateTime")
    List<ScheduledOwnExpense> findAllWithNextTriggerDateBefore(
            @Param("creationDateTime") LocalDate creationDateTime);
}
