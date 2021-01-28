package com.splitthebill.server.repository;

import com.splitthebill.server.model.expense.scheduled.Schedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
}
