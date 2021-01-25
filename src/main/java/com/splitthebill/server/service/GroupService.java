package com.splitthebill.server.service;

import com.splitthebill.server.dto.group.ExpenseParticipantCreateDto;
import com.splitthebill.server.dto.group.GroupCreateDto;
import com.splitthebill.server.dto.group.GroupExpenseCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.repository.CurrencyRepository;
import com.splitthebill.server.repository.GroupExpenseRepository;
import com.splitthebill.server.repository.GroupRepository;
import com.splitthebill.server.repository.PersonGroupRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class GroupService {

    @NonNull
    private final GroupRepository groupRepository;

    @NonNull
    private final GroupExpenseRepository groupExpenseRepository;

    @NonNull
    private final PersonService personService;

    @NonNull
    private final PersonGroupRepository personGroupRepository;

    @NonNull
    private final CurrencyRepository currencyRepository;

    public Group createGroup(GroupCreateDto groupDto) throws EntityNotFoundException {
        Group group = new Group();
        group.setName(groupDto.name);
        group = groupRepository.save(group);
        LinkedList<PersonGroup> members = new LinkedList<>();
        for (Long id : groupDto.membersIds) {
            Person person = personService.getPersonById(id);
            PersonGroup personGroup = new PersonGroup(person, group);
            personGroupRepository.save(personGroup);
            members.add(personGroup);
        }
        group.setMembers(members);
        group = groupRepository.save(group);
        return group;
    }

    public Group getGroupById(Long id) throws EntityNotFoundException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group has not been found."));
    }

    public void addExpense(Long groupId, GroupExpenseCreateDto expenseDto) throws EntityNotFoundException {
        GroupExpense groupExpense = new GroupExpense();
        Group group = getGroupById(groupId);
        Currency currency = currencyRepository.findCurrencyByAbbreviation(expenseDto.currencyAbbreviation)
                .orElseThrow(EntityNotFoundException::new);
        groupExpense.setGroup(group);
        groupExpense.setTitle(expenseDto.title);
        groupExpense.setCurrency(currency);
        PersonGroup creditor = personGroupRepository.findById(expenseDto.creditorId)
                .orElseThrow(() -> new EntityNotFoundException("Creditor has not been found"));
        groupExpense.setCreditor(creditor);
        BigDecimal amount = BigDecimal.valueOf(expenseDto.amount);
        groupExpense.setAmount(amount);
        LinkedList<PersonGroupExpense> debtors = new LinkedList<>();
        for (ExpenseParticipantCreateDto participant : expenseDto.debtors) {
            PersonGroup person = personGroupRepository.findById(participant.debtorId)
                    .orElseThrow(() -> new EntityNotFoundException("Debtor has not been found"));
            PersonGroupExpense personExpense = new PersonGroupExpense(participant.splitRatio, person, groupExpense);
            debtors.add(personExpense);
        }
        groupExpense.setPersonGroupExpenses(debtors);
        group.addExpense(groupExpense);
        groupExpenseRepository.save(groupExpense);
    }

    public void addGroupMember(Long groupId, Long personId) {
        Group group = this.getGroupById(groupId);
        Person person = personService.getPersonById(personId);
        PersonGroup member = new PersonGroup(person, group);
        personGroupRepository.save(member);
        group.addMember(member);
        groupRepository.save(group);
    }
}
