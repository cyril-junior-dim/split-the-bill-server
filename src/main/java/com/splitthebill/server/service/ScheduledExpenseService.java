package com.splitthebill.server.service;

import com.splitthebill.server.dto.expense.scheduled.own.ScheduledOwnExpenseCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.expense.scheduled.FrequencyUnit;
import com.splitthebill.server.model.expense.scheduled.Schedule;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.repository.CurrencyRepository;
import com.splitthebill.server.repository.ScheduledGroupExpenseRepository;
import com.splitthebill.server.repository.ScheduledOwnExpenseRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledExpenseService {

    @NonNull
    private final ScheduledGroupExpenseRepository scheduledGroupExpenseRepository;
    @NonNull
    private final ScheduledOwnExpenseRepository scheduledOwnExpenseRepository;
    @NonNull
    private final CurrencyRepository currencyRepository;

    public List<ScheduledGroupExpense> getAllScheduledGroupExpenses(Person person) {
        return scheduledGroupExpenseRepository.findAllByCreditor_Person(person);
    }

    public List<ScheduledOwnExpense> getAllScheduledOwnExpenses(Person person) {
        return scheduledOwnExpenseRepository.findAllByPerson(person);
    }

    public ScheduledOwnExpense scheduledOwnExpense(Person person, ScheduledOwnExpenseCreateDto createDto) {
        ScheduledOwnExpense scheduledOwnExpense = new ScheduledOwnExpense();
        Currency currency = currencyRepository.findCurrencyByAbbreviation(createDto.currency)
                .orElseThrow(EntityNotFoundException::new);
        scheduledOwnExpense.setCurrency(currency);
        scheduledOwnExpense.setPerson(person);
        BigDecimal amount = createDto.amount;
        scheduledOwnExpense.setAmount(amount);

        FrequencyUnit frequencyUnit;
        try {
            frequencyUnit = FrequencyUnit.valueOf(createDto.schedule.frequencyUnit);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Incorrect frequency unit. Should be 'DAY', 'WEEK', 'MONTH' or 'YEAR'.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate nextTriggerDate;
        try {
            nextTriggerDate = LocalDate.parse(createDto.schedule.nextTrigger, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Next trigger date in wrong format. Required format: dd-MM-yyyy");
        }

        Schedule schedule = Schedule.builder()
                .amount(createDto.schedule.amount)
                .frequencyUnit(frequencyUnit)
                .nextTrigger(nextTriggerDate)
                .build();
        scheduledOwnExpense.setTitle(createDto.title);
        scheduledOwnExpense.setSchedule(schedule);
        return scheduledOwnExpenseRepository.save(scheduledOwnExpense);
    }

    public void deleteScheduledOwnExpense(Long expenseId, Person issuer) throws IllegalAccessException {
        ScheduledOwnExpense expense = scheduledOwnExpenseRepository.findById(expenseId)
                .orElseThrow(EntityNotFoundException::new);

        // Check if issuer is expense creditor
        if (!issuer.getId().equals(expense.getPerson().getId()))
            throw new IllegalAccessException("Must be a creditor of an expense in order to remove it.");

        scheduledOwnExpenseRepository.delete(expense);
    }
}
