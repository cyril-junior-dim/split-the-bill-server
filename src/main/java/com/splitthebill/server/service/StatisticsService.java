package com.splitthebill.server.service;

import com.splitthebill.server.dto.group.GroupExpenseReadDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.repository.GroupExpenseRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    @NonNull GroupExpenseRepository groupExpenseRepository;

    public Map<String, List<GroupExpenseReadDto>> getAllPersonExpenses(Person person) {
        List<GroupExpense> personExpenses = groupExpenseRepository.getAllByCreditor_Id(person.getId());
        return personExpenses.stream().map(GroupExpenseReadDto::new).collect(Collectors.groupingBy(GroupExpenseReadDto::getCurrency));
    }

}
